package org.bryanthedragon.morph.network.server.survival;

import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.common.survival.PacketFavorite;
import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;

import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerFavorite extends ServerMessageHandler<PacketFavorite>
{
    @Override
    public void run(ServerPlayer player, PacketFavorite message)
    {
        Morphing.get(player).favorite(message.index);
        Dispatcher.sendTo(message, player);
    }
}