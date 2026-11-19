package org.bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;

/**
 * Jumping ability
 * 
 * Makes player jump whenever he moves and on the ground. Just like a slime!
 */
public class Jumping extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        boolean moving = target.xxa != 0 || target.zza != 0;

        if (target.onGround() && moving && !target.isInWater())
        {
            target.setDeltaMovement(target.getDeltaMovement().x, 0.5D, target.getDeltaMovement().z);
        }
    }
}
