package org.bryanthedragon.morph.api.attacks;

import org.bryanthedragon.morph.api.abilities.IAttackAbility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

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
            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
    }
}