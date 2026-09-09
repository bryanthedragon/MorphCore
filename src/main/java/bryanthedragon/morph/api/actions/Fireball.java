package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;

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

        if (world.isClientSide)
        {
            return;
        }

        if (target instanceof Player player && player.getAttackStrengthScale(0.0F) < 1)
        {
            return;
        }

        Vec3 look = target.getViewVector(1.0F);

        double d1 = 4.0D;
        double d2 = look.x * d1;
        double d3 = look.y * d1;
        double d4 = look.z * d1;

        world.levelEvent(null, LevelEvent.SOUND_GHAST_FIREBALL, target.blockPosition(), 0);

        LargeFireball fireball = new LargeFireball(world, target, d2, d3, d4, 1);

        fireball.setPos(target.getX(), target.getY() + target.getBbHeight() * 0.9, target.getZ());

        world.addFreshEntity(fireball);

        if (target instanceof Player player)
        {
            player.resetAttackStrengthTicker();
        }
    }
}
