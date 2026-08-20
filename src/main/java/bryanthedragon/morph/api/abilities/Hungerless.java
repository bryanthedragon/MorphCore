package bryanthedragon.vanilla_pack.abilities;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.init.MobEffects;

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
        if (target.isPotionActive(MobEffects.HUNGER))
        {
            this.onMorph(target);
        }
    }

    @Override
    public void onMorph(LivingEntity target)
    {
        target.removePotionEffect(MobEffects.HUNGER);
    }
}