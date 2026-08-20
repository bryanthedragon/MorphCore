package bryanthedragon.morph.network.server.creative;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.creative.PacketClearAcquired;
import net.minecraft.entity.player.ServerPlayer;

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