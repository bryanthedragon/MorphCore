package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Ender dragon's fire breath 
 */
public class FireBreath implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        if (target.level().isClientSide)
        {
            return;
        }

        if (target instanceof Player && ((Player) target).getAttackStrengthScale(0.0F) < 1)
        {
            return;
        }

        Vec3 vec3d = target.getLookAngle();

        double d1 = 4.0D;
        double d2 = vec3d.x * d1;
        double d3 = vec3d.y * d1;
        double d4 = vec3d.z * d1;

        target.level().playEvent((Player) null, 1017, new BlockPos(target), 0);

        DragonFireball fireball = new DragonFireball(target.level(), target, d2, d3, d4);

        fireball.setPos(target.getX() + d2 / d1, target.getY() + target.getBbHeight() * 0.9, target.getZ() + d4 / d1);

        target.level().getEntity(fireball);

        if (target instanceof Player)
        {
            ((Player) target).resetCooldown();
        }
    }
}