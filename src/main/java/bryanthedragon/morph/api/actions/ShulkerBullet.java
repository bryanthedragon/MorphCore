package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.EntityUtils;
import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;

public class ShulkerBullet implements IAction
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

        Entity toShoot = EntityUtils.getTargetEntity(target, 32);

        if (toShoot != null)
        {
            target.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.2F + 1.0F);

            EntityShulkerBullet fireball = new EntityShulkerBullet(world, target, toShoot, Axis.Z);

            fireball.posX = target.posX;
            fireball.posZ = target.posZ;

            world.spawnEntity(fireball);
        }

        if (target instanceof Player)
        {
            ((Player) target).resetCooldown();
        }
    }
}