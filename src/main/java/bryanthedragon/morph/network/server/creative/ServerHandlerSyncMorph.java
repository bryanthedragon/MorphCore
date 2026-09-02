package bryanthedragon.morph.network.server.creative;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.creative.PacketSyncMorph;
import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerSyncMorph extends ServerMessageHandler<PacketSyncMorph>
{
    @Override
    public void run(ServerPlayer player, PacketSyncMorph message)
    {
        IMorphing cap = Morphing.get(player);

        if (cap != null && message.morph != null)
        {
            cap.getAcquiredMorphs().set(message.index, message.morph);
        }
    }
}