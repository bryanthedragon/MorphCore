package org.bryanthedragon.morph.api.abilities;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * Hungerless ability
 * 
 * This ability is responsible for removing hunger potion effect from given 
 * morph. Really good for zombies, since they're the only one who're going 
 * to use them.
 */
public class Hungerless extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        if (target.hasEffect(MobEffects.HUNGER))
        {
            this.onMorph(target);
        }
    }

    @Override
    public void onMorph(LivingEntity target)
    {
        target.removeEffect(MobEffects.HUNGER);
    }
}