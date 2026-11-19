package org.bryanthedragon.morph.network.server.survival;

import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.common.survival.PacketKeybind;
import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;

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