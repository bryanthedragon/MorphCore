package bryanthedragon.morph.api.attacks;

import bryanthedragon.morph.api.abilities.IAttackAbility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

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
            ((LivingEntity) target).addPotionEffect(new PotionEffect(MobEffects.POISON, 200));
        }
    }
}