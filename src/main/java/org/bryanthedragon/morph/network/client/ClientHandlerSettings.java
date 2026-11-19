package org.bryanthedragon.morph.network.client;

import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.network.common.PacketSettings;

import net.minecraft.client.player.LocalPlayer;

public class ClientHandlerSettings extends ClientMessageHandler<PacketSettings>
{
    @Override
    public void run(LocalPlayer player, PacketSettings message)
    {
        MorphManager.INSTANCE.setActiveSettings(message.settings);
    }
}