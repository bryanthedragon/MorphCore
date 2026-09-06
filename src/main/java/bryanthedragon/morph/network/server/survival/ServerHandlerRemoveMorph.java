package bryanthedragon.morph.network.server.survival;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.morph.network.common.survival.PacketRemoveMorph;
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