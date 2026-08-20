package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Fireball action
 * 
 * This action is responsible for shooting a fireball from player's face. Used 
 * by ghast morph.
 */
public class Fireball implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        Level world = target.level();

        if (world.isRemote)
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

        world.playEvent((Player) null, 1016, new BlockPos(target), 0);

        EntityLargeFireball fireball = new EntityLargeFireball(world, target, d2, d3, d4);

        fireball.explosionPower = 1;
        fireball.posX = target.posX;
        fireball.posY = target.posY + target.height * 0.9;
        fireball.posZ = target.posZ;

        world.spawnEntity(fireball);

        if (target instanceof Player)
        {
            ((Player) target).resetCooldown();
        }
    }
}