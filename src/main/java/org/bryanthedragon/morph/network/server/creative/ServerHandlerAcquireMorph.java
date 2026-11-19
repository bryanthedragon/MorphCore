package org.bryanthedragon.morph.network.server.creative;

import org.bryanthedragon.morph.api.MorphAPI;
import org.bryanthedragon.morph.network.common.creative.PacketAcquireMorph;
import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server handler acquire morph
 * 
 * This handler is responsible for sending acquired morph for players in 
 * creative morph.
 */
public class ServerHandlerAcquireMorph extends ServerMessageHandler<PacketAcquireMorph>
{
    @Override
    public void run(ServerPlayer player, PacketAcquireMorph message)
    {
        if (player.isCreative() || player.isSpectator())
        {
            MorphAPI.acquire(player, message.morph);
        }
    }
}