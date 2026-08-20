package bryanthedragon.morph.capabilities.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.entity.LivingEntity;


public interface IModelRenderer
{    
    public void update(LivingEntity entity);

    @OnlyIn(Dist.CLIENT)
    public void updateSelector(LivingEntity entity);

    public boolean canRender();

    public boolean render(LivingEntity entity, double x, double y, double z, float partialTicks);
}