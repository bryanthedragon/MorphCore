package bryanthedragon.morph.network.server.creative;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.network.common.creative.PacketAcquireMorph;
import net.minecraft.entity.player.PlayerMP;

/**
 * Server handler acquire morph
 * 
 * This handler is responsible for sending acquired morph for players in 
 * creative morph.
 */
public class ServerHandlerAcquireMorph extends ServerMessageHandler<PacketAcquireMorph>
{
    @Override
    public void run(PlayerMP player, PacketAcquireMorph message)
    {
        if (player.isCreative() || player.isSpectator())
        {
            MorphAPI.acquire(player, message.morph);
        }
    }
}