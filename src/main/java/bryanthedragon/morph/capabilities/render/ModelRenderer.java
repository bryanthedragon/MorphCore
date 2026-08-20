package bryanthedragon.morph.capabilities.render;

import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.client.EntityModelHandler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModelRenderer implements IModelRenderer
{
    public static long selectorsUpdate = System.currentTimeMillis();

    public EntitySelector selector;
    public AbstractMorph morph;
    public long lastUpdate = -1;

    private int timer;
    private float lastW = -1;
    private float lastH = -1;

    public static IModelRenderer get(Entity entity)
    {
        return (IModelRenderer) entity.getCapability(ModelProvider.MODEL, null);
    }

    @Override
    public void update(LivingEntity target)
    {
        if (this.lastUpdate < selectorsUpdate || this.IsNotMatchedAnymore(target))
        {
            this.lastUpdate = selectorsUpdate;

            this.updateSelector(target);
        }

        if (this.selector != null && this.morph != null)
        {
            this.morph.update(target);
        }
    }

    private boolean IsNotMatchedAnymore(LivingEntity target)
    {
        this.timer += 1;

        if (this.timer > 10)
        {
            this.timer = 0;

            if (this.selector == null)
            {
                return true;
            }

            return !this.selector.matches(target);
        }

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateSelector(LivingEntity target)
    {
        this.selector = null;
        this.morph = null;

        for (EntitySelector selector : EntityModelHandler.selectors)
        {
            if (selector.matches(target))
            {
                this.selector = selector;
                this.morph = MorphManager.INSTANCE.morphFromNBT(this.selector.morph);

                if (this.lastW < 0)
                {
                    this.lastW = (float) target.yOld;
                    this.lastH = (float) target.yOld;
                }

                return;
            }
        }

        if (this.selector == null && this.lastW > 0 && this.lastH > 0)
        {
            target.xOld = this.lastW;
            target.yOld = this.lastH;

            this.lastW = -1;
            this.lastH = -1;
        }
    }

    @Override
    public boolean canRender()
    {
        return this.selector != null;
    }

    /**
     * Render the animator controller based on given entity
     */
    @Override
    public boolean render(LivingEntity entity, double x, double y, double z, float partialTicks)
    {
        return this.selector != null && MorphUtils.render(this.morph, entity, x, y, z, 0, partialTicks);
    }
}