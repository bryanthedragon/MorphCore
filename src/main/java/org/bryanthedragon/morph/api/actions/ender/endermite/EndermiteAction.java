package org.bryanthedragon.morph.api.actions.ender.endermite;

import java.util.Random;

import javax.annotation.Nullable;

import org.bryanthedragon.morph.api.abilities.IAction;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;


/**
 * Endermite action
 * 
 * Teleports player to a random location nearby.
 */
public class EndermiteAction implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        Random rand = (Random) target.getRandom();

        /* Teleports within 32 block radius */
        double x = target.getX() + (rand.nextDouble() - 0.5D) * 64.0D;
        double y = target.getY() + (double) (rand.nextInt(64) - 32);
        double z = target.getZ() + (rand.nextDouble() - 0.5D) * 64.0D;

        if (target.randomTeleport(x, y, z, true))
        {
            target.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            target.level().playSound((Player) null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENDERMAN_TELEPORT, target.getSoundSource(), 1.0F, 1.0F);
        }
    }
}