package org.bryanthedragon.morph.api.actions.overworld.passive.snowgolem;

import javax.annotation.Nullable;

import org.bryanthedragon.morph.api.abilities.IAction;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Snowball action
 * 
 * Throws a snowball in the look direction of the player. The code is taken 
 * from {@link Fireball#execute(LivingEntity, AbstractMorph)} and 
 * {@link net.minecraft.world.entity.animal.SnowGolem#performRangedAttack(LivingEntity, float)}
 */
public class SnowballAction implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        if (target.level().isClientSide)
        {
            return;
        }

        net.minecraft.world.entity.projectile.Snowball snowball = new net.minecraft.world.entity.projectile.Snowball(target.level(), target);
        Vec3 look = target.getLookAngle();

        double d1 = 4.0D;
        double d2 = look.x * d1;
        double d3 = look.y * d1;
        double d4 = look.z * d1;

        snowball.moveTo(target.getX(), target.getY() + target.getEyeHeight() * 0.9F, target.getZ(), target.getYRot(), target.getXRot());
        snowball.setDeltaMovement(d2, d3, d4);

        target.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 1.0F / (target.getRandom().nextFloat() * 0.4F + 0.8F));
        target.level().addFreshEntity(snowball);
    }
}
