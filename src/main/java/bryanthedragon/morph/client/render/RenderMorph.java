package bryanthedragon.morph.client.render;

import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.entity.EntityMorph;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMorph extends EntityRenderer<EntityMorph>
{
    public RenderMorph(EntityRendererProvider.Context context)
    {
        super(context);
        this.shadowRadius = 0.35F;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMorph entity)
    {
        return null;
    }

    @Override
    public void render(EntityMorph entity, float entityYaw, float partialTicks, PoseStack poseStack,
        MultiBufferSource buffer, int packedLight)
    {
        if (entity.morph == null)
        {
            return;
        }

        float range = (entity.timer - partialTicks) / 30.0F;
        float alpha = 0.7F - (range <= 0 ? 0.0F : range) * 0.7F;
        float ticks = entity.tickCount + partialTicks;
        float scale = Math.max(0.0F, alpha);

        poseStack.pushPose();
        poseStack.translate(0.0D, Math.sin(ticks / 5.0F) * 0.1D + 0.2D, 0.0D);
        poseStack.scale(scale, scale, scale);
        MorphUtils.render(entity.morph, entity, 0.0D, 0.0D, 0.0D, entityYaw, partialTicks);
        poseStack.popPose();
    }
}
