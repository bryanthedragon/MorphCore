package bryanthedragon.morph.network.server.survival;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.morph.network.common.survival.PacketFavorite;
import net.minecraft.entity.player.ServerPlayer;

public class ServerHandlerFavorite extends ServerMessageHandler<PacketFavorite>
{
    @Override
    public void run(ServerPlayer player, PacketFavorite message)
    {
        Morphing.get(player).favorite(message.index);
        Dispatcher.sendTo(message, player);
    }
}