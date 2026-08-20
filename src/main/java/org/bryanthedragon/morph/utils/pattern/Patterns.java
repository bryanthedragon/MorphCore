package bryanthedragon.morph.utils.pattern;

import java.util.regex.Pattern;

public class Patterns
{
    public static final Pattern FILENAME = Pattern.compile("^[\\w\\d-_.\\[\\]!@#$%^&()]*$");
}