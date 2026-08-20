package bryanthedragon.morph.api.attacks;

import bryanthedragon.morph.api.MorphHandler;
import bryanthedragon.morph.api.abilities.IAttackAbility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Knock back attack
 * 
 * This makes the entity go up in the air. Really cool!
 */
public class KnockbackAttack implements IAttackAbility
{
    @Override
    public void attack(final Entity target, LivingEntity source)
    {
        final Vec3 look = source.getViewVector(1.0F);
        final double d = 1;

        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                target.setDeltaMovement(look.x * d, 1, look.z * d);
            }
        };

        if (!source.level().isClientSide)
        {
            MorphHandler.FUTURE_TASKS_SERVER.add(task);
        }
        else
        {
            MorphHandler.FUTURE_TASKS_CLIENT.add(task);
        }
    }
}