package bryanthedragon.vanilla_pack.abilities;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;

/**
 * Glide ability
 * 
 * This ability makes player fall much slower. Sneak to disable gliding effect.
 */
public class Glide extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        boolean isFlying = target instanceof Player && ((Player) target).capabilities.isFlying;

        if (!target.onGround && target.motionY < 0.0D && !isFlying && !target.isElytraFlying() && !target.isSneaking())
        {
            target.motionY *= 0.6D;
            target.fallDistance = 0.0F;
        }
    }
}