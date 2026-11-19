package org.bryanthedragon.morph.api.actions.overworld.passive.horse;

import javax.annotation.Nullable;

import org.bryanthedragon.morph.api.abilities.IAction;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;

/**
 * Jump action
 * 
 * Makes player jump very high like a <s>horse</s> rabbit. The strength of 
 * this jump is about 3 blocks high. 
 */
public class JumpBoostAction implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        if (target.onGround() && !target.isInWater())
        {
            target.setDeltaMovement(target.getDeltaMovement().x * 4.0, 0.75, target.getDeltaMovement().z * 4.0);
        }
    }
}