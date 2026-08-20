package bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;

/**
 * Step Up ability
 * 
 * This ability makes player walk up blocks, like a horse.
 */
public class StepUp extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        target.setMaxUpStep(1.0F);
    }
    
    @Override
    public void onMorph(LivingEntity target)
    {
        target.setMaxUpStep(1.0F);
    }
    
    @Override
    public void onDemorph(LivingEntity target)
    {
        target.setMaxUpStep(0.6F);
    }
}