package bryanthedragon.morph.utils.obfuscated;

import javax.annotation.Nonnull;

import bryanthedragon.morph.utils.mappings.MappingsUtil.DevMappings;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Object for storing a method/field string. If a srgname
 * is given, it will provide the appropriate mcp name if
 * in a development environment environment. Otherwise
 * it will just provide the given string.
 * 
 * @author asanetargoss
 */

public class ObfuscatedUtils
{
    public static class ObfuscatedName
    {
        private final String srgName;
        /** Initially null. Given cached value when get()
         * is called in a development environment */
        private String mcpName = null;
        
        public ObfuscatedName(@Nonnull String srgName)
        {
            this.srgName = srgName;
        }
        
        /**
         * Get name relevant for current environment.
         * In development environment, default to srgName
         * if mcpName is not present in the mapping.
         */
        public @Nonnull String getName()
        {
            if (FMLEnvironment.production)
            {
                return srgName;
            }
            else
            {
                if (mcpName == null)
                {
                    mcpName = DevMappings.get(srgName);
                    if (mcpName == null)
                    {
                        mcpName = srgName;
                    }
                }
                return mcpName;
            }
        }
    }
}