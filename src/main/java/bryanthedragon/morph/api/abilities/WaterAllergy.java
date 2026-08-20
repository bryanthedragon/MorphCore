package bryanthedragon.morph.api.abilities;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Water allergy ability
 * 
 * This ability is responsible for damaging the player when he is in the water 
 * primarily will be used in Enderman's morph.
 * 
 * This is more like a disability than an ability *Ba-dum-pam-dum-tsss*
 */
public class WaterAllergy extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        if (target.isWet())
        {
            target.attackEntityFrom(DamageSource.DROWN, 1.0F);
        }
    }
}