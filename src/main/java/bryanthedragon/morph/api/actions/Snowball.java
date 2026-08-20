package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

/**
 * Snowball action
 * 
 * Throws a snowball in the look direction of the player. The code is taken 
 * from {@link Fireball#execute(Player)} and 
 * {@link EntitySnowman#attackEntityWithRangedAttack(net.minecraft.entity.LivingEntity, float)}
 */
public class Snowball implements IAction
{
    public Snowball(Level level, LivingEntity target) {
        
    }

    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        if (!target.level().isClientSide)
        {
            Snowball snowball = new Snowball(target.level(), target);
            Vec3 vec3d = target.getLookAngle();

            double d1 = 4.0D;
            double d2 = vec3d.x * d1;
            double d3 = vec3d.y * d1;
            double d4 = vec3d.z * d1;

            snowball.SetPos(target.getX(), target.getY() + target.getEyeHeight() * 0.9F, target.getZ(), target.getYRot(), target.getXRot());
            snowball.setDeltaMovement(d2, d3, d4);

            target.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 1.0F / (target.getRandom().nextFloat() * 0.4F + 0.8F));
            target.level().addFreshEntity(snowball);
        }
    }
}