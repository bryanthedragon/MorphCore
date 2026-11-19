package org.bryanthedragon.morph.api.attacks;

import org.bryanthedragon.morph.api.abilities.IAttackAbility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Poison attack
 * 
 * This attack poisons the target. Used by cave spider morph in the main mod.
 */
public class PoisonAttack implements IAttackAbility
{
    @Override
    public void attack(Entity target, LivingEntity source)
    {
        if (target instanceof LivingEntity)
        {
            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        }
    }
}