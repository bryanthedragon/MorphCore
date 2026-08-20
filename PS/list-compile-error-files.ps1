param(
    [string]$JavaHome,
    [string]$LogPath
)

$projectRoot = $PSScriptRoot
while (-not (Test-Path (Join-Path $projectRoot "gradlew.bat"))) {
    $parentDirectory = Split-Path -Parent $projectRoot
    if (-not $parentDirectory -or $parentDirectory -eq $projectRoot) {
        throw "Could not locate gradlew.bat in this directory or its parents."
    }

    $projectRoot = $parentDirectory
}

$gradleWrapper = Join-Path $projectRoot "gradlew.bat"

if (-not $JavaHome -or -not (Test-Path (Join-Path $JavaHome "bin\java.exe"))) {
    $JavaHome = $env:JAVA_HOME
}

if (-not $JavaHome -or -not (Test-Path (Join-Path $JavaHome "bin\java.exe"))) {
    $settingsPath = Join-Path $projectRoot ".vscode\settings.json"
    if (Test-Path $settingsPath) {
        $settings = Get-Content $settingsPath -Raw | ConvertFrom-Json
        $JavaHome = $settings.'java.import.gradle.java.home'
    }
}

if (-not $JavaHome -or -not (Test-Path (Join-Path $JavaHome "bin\java.exe"))) {
    throw "Could not find a valid JDK. Pass -JavaHome or set JAVA_HOME to a JDK directory."
}

if (-not $LogPath) {
    $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $LogPath = Join-Path $projectRoot "build\compile-errors-$timestamp.log"
}

$logDirectory = Split-Path -Parent $LogPath
if ($logDirectory -and -not (Test-Path $logDirectory)) {
    New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null
}

$previousJavaHome = $env:JAVA_HOME
try {
    $env:JAVA_HOME = $JavaHome
    Push-Location $projectRoot
    try {
        $buildOutput = & $gradleWrapper compileJava --console=plain 2>&1
        $gradleExitCode = $LASTEXITCODE
    }
    finally {
        Pop-Location
    }
}
finally {
    $env:JAVA_HOME = $previousJavaHome
}

$buildOutput | Out-File -FilePath $LogPath -Encoding utf8
$diagnostics = foreach ($outputLine in $buildOutput) {
    $line = [string]$outputLine
    if ($line -match '^\s*(?<path>(?:[A-Za-z]:\\|\.\\|\.\./|src/|src\\).*?\.java):(?<line>\d+): error:') {
        $file = $Matches.path
        if (-not [System.IO.Path]::IsPathRooted($file)) {
            $file = Join-Path $projectRoot $file
        }

        [PSCustomObject]@{
            File = [System.IO.Path]::GetFullPath($file)
            Line = [int]$Matches.line
        }
    }
}

$uniqueFiles = @($diagnostics | Select-Object -ExpandProperty File -Unique | Sort-Object)
if ($uniqueFiles.Count -gt 0) {
    Write-Output "Compiler errors found in $($uniqueFiles.Count) files ($(@($diagnostics).Count) error locations):"
    $uniqueFiles | ForEach-Object { Write-Output "  $_" }
    Write-Output "Full Gradle output: $LogPath"
    exit 1
}

if ($gradleExitCode -eq 0) {
    Write-Output "compileJava succeeded; no compiler-error files found."
}
else {
    Write-Output "Gradle failed before reporting Java compiler errors (exit code $gradleExitCode)."
    Write-Output "Check the full Gradle output: $LogPath"
}

exit $gradleExitCode