package org.bryanthedragon.morph.network.server.creative;

import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.common.creative.PacketClearAcquired;
import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;

import net.minecraft.server.level.ServerPlayer;


public class ServerHandlerClearAcquired extends ServerMessageHandler<PacketClearAcquired>
{
    @Override
    public void run(ServerPlayer player, PacketClearAcquired message)
    {
        IMorphing cap = Morphing.get(player);

        if (cap != null)
        {
            cap.removeAcquired();
        }
    }
}