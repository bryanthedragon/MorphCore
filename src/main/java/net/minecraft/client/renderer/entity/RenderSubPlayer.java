package net.minecraft.client.renderer.entity;

import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;

/**
 * Render sub player renderer
 *
 * This class is responsible for substituting native player renderer classes
 * in the skinMap to achieve the rendering of the custom morphed hands.
 *
 * I hope nobody will want to substitute the same map as I did :D
 */
public class RenderSubPlayer extends RenderPlayer
{
    public RenderPlayer original;

    /**
     * Initiate with render manager, player renderer and smallArms flag.
     */
    public RenderSubPlayer(RenderManager renderManager, RenderPlayer original, boolean smallArms)
    {
        super(renderManager, smallArms);
        this.original = original;
        // This improves compat with render override code that thinks this is the original RenderPlayer
        this.layerRenderers = original.layerRenderers;
    }

    /**
     * Should make layers added to the original player renderer
     */
    @Override
    public <V extends LivingEntity, U extends LayerRenderer<V>> boolean addLayer(U layer)
    {
        if (this.original == null)
        {
            return false;
        }
        
        return this.original.addLayer(layer);
    }

    /**
     * Render default or morphed left hand.
     */
    @Override
    public void renderLeftArm(AbstractClientPlayer clientPlayer)
    {
        if (!this.renderHand(clientPlayer, EnumHand.OFF_HAND))
        {
            this.original.renderLeftArm(clientPlayer);
        }
    }

    /**
     * Render default or morphed right hand.
     */
    @Override
    public void renderRightArm(AbstractClientPlayer clientPlayer)
    {
        if (!this.renderHand(clientPlayer, EnumHand.MAIN_HAND))
        {
            this.original.renderRightArm(clientPlayer);
        }
    }

    private boolean renderHand(Player player, EnumHand hand)
    {
        if (MorphCore.disableFirstPersonHand.get())
        {
            return false;
        }

        IMorphing cap = Morphing.get(player);

        if (cap != null && cap.isMorphed())
        {
            AbstractMorph morph = cap.getCurrentMorph();

            try
            {
                if (!morph.errorRendering && morph.renderHand(player, hand))
                {
                    return true;
                }
            }
            catch (Exception e)
            {
                morph.errorRendering = true;
            }
        }

        return false;
    }

    /* Overriding RenderPlayer methods */

    @Override
    public PlayerModel<Player> getMainModel()
    {
        return this.original == null ? super.getMainModel() : this.original.getMainModel();
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.original.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity)
    {
        return this.original.getEntityTexture(entity);
    }

    @Override
    public void transformHeldFull3DItemLayer()
    {
        this.original.transformHeldFull3DItemLayer();
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        this.original.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }

    @Override
    protected void renderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq)
    {
        this.original.renderEntityName(entityIn, x, y, z, name, distanceSq);
    }

    @Override
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z)
    {
        this.original.renderLivingAt(entityLivingBaseIn, x, y, z);
    }

    @Override
    protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        this.original.applyRotations(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    }
}