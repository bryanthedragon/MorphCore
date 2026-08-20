package bryanthedragon.morph.network.server.survival;

import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.morph.network.common.survival.PacketKeybind;
import bryanthedragon.morph.network.server.message.ServerMessageHandler;

import net.minecraft.server.level.ServerPlayer;


public class ServerHandlerKeybind extends ServerMessageHandler<PacketKeybind>
{
    @Override
    public void run(ServerPlayer player, PacketKeybind message)
    {
        Morphing.get(player).keybind(message.index, message.keybind);
        Dispatcher.sendTo(message, player);
    }
}