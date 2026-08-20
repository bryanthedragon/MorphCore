package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

/**
 * Small fireball action
 * 
 * This action is responsible for shooting a fireball from player's face. Used 
 * by blaze morph.
 */
public class SmallFireball implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        Level world = target.level();

        if (world.isClientSide)
        {
            return;
        }

        if (target instanceof Player && ((Player) target).getAttackStrengthScale(0.0F) < 1)
        {
            return;
        }

        Vec3 vec3 = target.getLookAngle();

        double d1 = 4.0D;
        double d2 = vec3.x * d1;
        double d3 = vec3.y * d1;
        double d4 = vec3.z * d1;

        world.playSound(null, target.getX(), target.getY(), target.getZ(), net.minecraft.sounds.SoundEvents.FIRECHARGE_USE, net.minecraft.sounds.SoundSource.PLAYERS, 0.5F, 0.4F / (net.minecraft.util.RandomSource.create().nextFloat() * 0.4F + 0.8F));

        SmallFireball fireball = new SmallFireball(world, target, d2, d3, d4);

        fireball.setPos(target.getX(), target.getY() + target.getEyeHeight() * 0.9, target.getZ());

        world.addFreshEntity(fireball);

        if (target instanceof Player)
        {
            ((Player) target).resetAttackStrengthTicker();
        }
    }
}