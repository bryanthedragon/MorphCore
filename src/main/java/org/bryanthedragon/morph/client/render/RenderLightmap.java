package bryanthedragon.morph.client.render;

import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render brightness
 * 
 * This class is a workaround class which allows using lightmap methods 
 * without having to resort to straight copy-pasting the code.
 */
@OnlyIn(Dist.CLIENT)
public final class RenderLightmap
{
    private RenderLightmap()
    {
    }

    public static boolean canRenderNamePlate(LivingEntity entity)
    {
        return entity.shouldShowName();
    }
}