package bryanthedragon.morph.utils.reflection;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import bryanthedragon.morph.utils.helpers.optifine.OptifineHelper;

public class ReflectionUtils
{
    /**
     * Minecraft texture manager's field to the texture map (a map of 
    * {@link AbstractTexture} which is used to cache references to 
     * OpenGL textures). 
     */
    public static Field TEXTURE_MAP;

    /**
     * Whether isShadowPass field was checked
     */
    private static boolean SHADOW_PASS_CHECK;

    /**
     * Optifine's shadow pass field
     */
    private static Field SHADOW_PASS;

    /**
     * Get texture map from texture manager using reflection API
     */
    @SuppressWarnings("unchecked")
    public static Map<ResourceLocation, AbstractTexture> getTextures(TextureManager manager)
    {
        if (TEXTURE_MAP == null)
        {
            setupTextureMapField(manager);
        }

        try
        {
            return (Map<ResourceLocation, AbstractTexture>) TEXTURE_MAP.get(manager);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Setup texture map field which is looked up using the reflection API
     */
    @SuppressWarnings("rawtypes")
    public static void setupTextureMapField(TextureManager manager)
    {
        /* Finding the field which has holds the texture cache */
        for (Field field : manager.getClass().getDeclaredFields())
        {
            if (Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }

            field.setAccessible(true);

            try
            {
                Object value = field.get(manager);

                if (value instanceof Map && ((Map) value).keySet().iterator().next() instanceof ResourceLocation)
                {
                    TEXTURE_MAP = field;

                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Use {@link OptifineHelper} class!
     */
    @SuppressWarnings("rawtypes")
    public static boolean isOptifineShadowPass()
    {
        if (!SHADOW_PASS_CHECK)
        {
            try
            {
                Class clazz = Class.forName("net.optifine.shaders.Shaders");

                SHADOW_PASS = clazz.getDeclaredField("isShadowPass");
            }
            catch (Exception e)
            {}

            SHADOW_PASS_CHECK = true;
        }

        if (SHADOW_PASS != null)
        {
            try
            {
                return (boolean) SHADOW_PASS.get(null);
            }
            catch (Exception e)
            {}
        }

        return false;
    }
}