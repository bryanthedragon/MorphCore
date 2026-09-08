package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.block.LevelEvent;
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

        if (target instanceof Player player && player.getAttackStrengthScale(0.0F) < 1)
        {
            return;
        }

        Vec3 look = target.getViewVector(1.0F);

        double d1 = 4.0D;
        double d2 = look.x * d1;
        double d3 = look.y * d1;
        double d4 = look.z * d1;

        target.level().levelEvent(null, LevelEvent.SOUND_DRAGON_FIREBALL, target.blockPosition(), 0);

        DragonFireball fireball = new DragonFireball(target.level(), target, d2, d3, d4);

        fireball.setPos(target.getX() + d2 / d1, target.getY() + target.getBbHeight() * 0.9, target.getZ() + d4 / d1);

        target.level().addFreshEntity(fireball);

        if (target instanceof Player player)
        {
            player.resetAttackStrengthTicker();
        }
    }
}
