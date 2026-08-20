package bryanthedragon.morph.api.abilities;

import bryanthedragon.morph.api.abilities.Ability;

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
        target.stepHeight = 1.0f;
    }
    
    @Override
    public void onMorph(LivingEntity target)
    {
        target.stepHeight = 1.0f;
    }
    
    @Override
    public void onDemorph(LivingEntity target)
    {
        target.stepHeight = 0.6f;
    }
}