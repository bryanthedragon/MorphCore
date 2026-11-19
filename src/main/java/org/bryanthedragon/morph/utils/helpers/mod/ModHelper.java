package org.bryanthedragon.morph.utils.helpers.mod;

import java.net.URISyntaxException;
import java.nio.file.Path;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

public class ModHelper
{
    public static ModContainer getCallerMod()
    {
        Class<?> caller = getCallerClass(2);

        if (caller == null) return null;

        if (caller.getProtectionDomain().getCodeSource() == null)
        {
            return null;
        }

        try
        {
            Path callerPath = Path.of(caller.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();
            ModContainer[] callerMod = new ModContainer[1];

            ModList.get().forEachModContainer((modId, mod) ->
            {
                Path modPath = mod.getModInfo().getOwningFile().getFile().getFilePath().toAbsolutePath().normalize();

                if (callerPath.equals(modPath))
                {
                    callerMod[0] = mod;
                }
            });

            return callerMod[0];
        }
        catch (URISyntaxException e)
        {}

        return null;
    }

    public static Class<?> getCallerClass(int offset)
    {
        StackTraceElement element = new Exception().getStackTrace()[offset + 1];

        try
        {
            return Class.forName(element.getClassName());
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }
}
