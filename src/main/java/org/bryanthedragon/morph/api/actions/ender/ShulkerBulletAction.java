package bryanthedragon.morph.api.actions.ender;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.EntityUtils;
import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ShulkerBulletAction implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        Level world = target.level();

        if (world.isClientSide())
        {
            return;
        }

        if (target instanceof Player player && player.getAttackStrengthScale(0.0F) < 1.0F)
        {
            return;
        }

        Entity toShoot = EntityUtils.getTargetEntity(target, 32);

        if (toShoot != null)
        {
            target.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (target.getRandom().nextFloat() - target.getRandom().nextFloat()) * 0.2F + 1.0F);

            net.minecraft.world.entity.projectile.ShulkerBullet bullet = new net.minecraft.world.entity.projectile.ShulkerBullet(world, target, toShoot, Direction.Axis.Z);

            bullet.setPos(target.getX(), target.getY(), target.getZ());

            world.addFreshEntity(bullet);
        }

        if (target instanceof Player player)
        {
            player.resetAttackStrengthTicker();
        }
    }
}
