package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Ender dragon's fire breath 
 */
public class FireBreath implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        if (target.level().isRemote)
        {
            return;
        }

        if (target instanceof Player && ((Player) target).getCooledAttackStrength(0.0F) < 1)
        {
            return;
        }

        Vec3d vec3d = target.getLook(1.0F);

        double d1 = 4.0D;
        double d2 = vec3d.x * d1;
        double d3 = vec3d.y * d1;
        double d4 = vec3d.z * d1;

        target.level().playEvent((Player) null, 1017, new BlockPos(target), 0);

        EntityDragonFireball fireball = new EntityDragonFireball(target.level(), target, d2, d3, d4);

        fireball.posX = target.posX + d2 / d1;
        fireball.posY = target.posY + target.height * 0.9;
        fireball.posZ = target.posZ + d4 / d1;

        target.level().spawnEntity(fireball);

        if (target instanceof Player)
        {
            ((Player) target).resetCooldown();
        }
    }
}