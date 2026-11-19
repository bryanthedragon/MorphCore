package org.bryanthedragon.morph.network.server.creative;

import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.common.creative.PacketSyncMorph;

import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerSyncMorph extends ServerMessageHandler<PacketSyncMorph>
{
    @Override
    public void run(ServerPlayer player, PacketSyncMorph message)
    {
        IMorphing cap = Morphing.get(player);

        if (cap != null && message.morph != null && (player.isCreative() || player.isSpectator())
            && message.index >= 0 && message.index < cap.getAcquiredMorphs().size())
        {
            cap.getAcquiredMorphs().set(message.index, message.morph);
        }
    }
}