package org.bryanthedragon.morph.network.server.survival;

import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.common.survival.PacketRemoveMorph;
import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;

import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerRemoveMorph extends ServerMessageHandler<PacketRemoveMorph>
{
    @Override
    public void run(ServerPlayer player, PacketRemoveMorph message)
    {
        if (Morphing.get(player).remove(message.index))
        {
            Dispatcher.sendTo(message, player);
        }
    }
}