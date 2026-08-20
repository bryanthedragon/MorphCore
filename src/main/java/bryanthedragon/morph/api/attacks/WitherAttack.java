package bryanthedragon.morph.api.attacks;

import bryanthedragon.morph.api.abilities.IAttackAbility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * Wither attack ability
 * 
 * This ability simple adds on a target wither effect
 */
public class WitherAttack implements IAttackAbility
{
    @Override
    public void attack(Entity target, LivingEntity source)
    {
        if (target instanceof LivingEntity)
        {
            ((LivingEntity) target).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
        }
    }
}