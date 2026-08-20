package bryanthedragon.vanilla_pack.abilities;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.MobEffects;

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
        if (target.isPotionActive(MobEffects.POISON))
        {
            this.onMorph(target);
        }
    }

    @Override
    public void onMorph(LivingEntity target)
    {
        target.removePotionEffect(MobEffects.POISON);
    }
}