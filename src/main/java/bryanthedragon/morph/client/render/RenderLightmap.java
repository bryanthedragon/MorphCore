package bryanthedragon.morph.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render brightness
 * 
 * This class is a workaround class which allows using lightmap methods 
 * without having to resort to straight copy-pasting the code.
 */
@OnlyIn(Dist.CLIENT)
public class RenderLightmap extends RenderLivingBase<LivingEntity>
{
    /**
     * Private instance 
     */
    private static RenderLightmap instance;

    public static RenderLightmap getInstance()
    {
        if (instance == null)
        {
            instance = new RenderLightmap(Minecraft.getInstance().getRenderManager(), null, 0);
        }

        return instance;
    }

    public static boolean canRenderNamePlate(LivingEntity entity)
    {
        return getInstance().canRenderName(entity);
    }

    public static boolean set(LivingEntity entity, float partialTicks)
    {
        return getInstance().setBrightness(entity, partialTicks, true);
    }

    public static void unset()
    {
        getInstance().unsetBrightness();
    }

    public RenderLightmap(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected int getColorMultiplier(LivingEntity entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        return 0;
    }

    @Override
    protected ResourceLocation getEntityTexture(LivingEntity entity)
    {
        return null;
    }
}