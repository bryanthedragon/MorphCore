package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

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

        if (world.isClientSide())
        {
            return;
        }

        if (target instanceof Player && ((Player) target).getAttackStrengthScale(0.0F) < 1.0F)
        {
            return;
        }

        Vec3 vec3d = target.getViewVector(1.0F);

        double d1 = 4.0D;
        double d2 = vec3d.x * d1;
        double d3 = vec3d.y * d1;
        double d4 = vec3d.z * d1;

        world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F);

        LargeFireball fireball = new LargeFireball(world, target, d2, d3, d4, 0);

        fireball.explosionPower = 1;
        fireball.setPos(target.getX(), target.getY() + target.getBbHeight() * 0.9, target.getZ());

        world.addFreshEntity(fireball);

        if (target instanceof Player)
        {
            ((Player) target).resetAttackStrengthTicker();
        }
    }
}