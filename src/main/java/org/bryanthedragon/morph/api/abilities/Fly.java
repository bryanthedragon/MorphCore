package org.bryanthedragon.morph.api.abilities;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Fly ability
 * 
 * Allows player to fly as in creative. Mostly used by flying (captain) morphs. 
 */
public class Fly extends Ability
{
    @Override
    public void onMorph(LivingEntity target)
    {
        if (target instanceof Player)
        {
            Player player = (Player) target;

            if (!player.getAbilities().mayfly)
            {
                player.getAbilities().mayfly = true;
                player.sendSystemMessage(Component.literal("Flying enabled"));
            }
        }
    }

    @Override
    public void update(LivingEntity target)
    {
        this.onMorph(target);
    }

    @Override
    public void onDemorph(LivingEntity target)
    {
        if (target instanceof Player)
        {
            Player player = (Player) target;

            if (!player.isCreative())
            {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.sendSystemMessage(Component.literal("Flying disabled"));
            }
        }
    }
}