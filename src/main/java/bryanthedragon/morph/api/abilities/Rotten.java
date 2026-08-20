package bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;

/**
 * Rotten ability
 * 
 * Prevents a character from being posion.
 * Mostly used for undead.
 */
public class Rotten extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        if (target.hasEffect(MobEffects.POISON))
        {
            this.onMorph(target);
        }
    }

    @Override
    public void onMorph(LivingEntity target)
    {
        target.removeEffect(MobEffects.POISON);
    }
}