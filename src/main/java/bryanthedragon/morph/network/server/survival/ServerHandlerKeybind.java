package bryanthedragon.morph.network.server.survival;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.morph.network.common.survival.PacketKeybind;
import net.minecraft.entity.player.PlayerMP;

public class ServerHandlerKeybind extends ServerMessageHandler<PacketKeybind>
{
    @Override
    public void run(PlayerMP player, PacketKeybind message)
    {
        Morphing.get(player).keybind(message.index, message.keybind);
        Dispatcher.sendTo(message, player);
    }
}