package org.bryanthedragon.morph.context.player;

import org.bryanthedragon.morph.context.Context;

import com.google.common.base.Preconditions;


import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PlayerContext extends Context
{
    private final Player player;

    public PlayerContext(Player ep)
    {
        player = Preconditions.checkNotNull(ep, "Player can't be null in PlayerContext!");
    }

    @Override
    public Level getWorld()
    {
        return player.level();
    }

    @Override
    public Player getPlayer()
    {
        return player;
    }
}