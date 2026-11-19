package org.bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

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
        boolean isFlying = target instanceof Player && ((Player) target).getAbilities().flying;

        if (!target.onGround() && target.getDeltaMovement().y < 0.0D && !isFlying && !target.isFallFlying() && !target.isShiftKeyDown())
        {
            target.setDeltaMovement(target.getDeltaMovement().x, target.getDeltaMovement().y * 0.6D, target.getDeltaMovement().z);
            target.fallDistance = 0.0F;
        }
    }
}