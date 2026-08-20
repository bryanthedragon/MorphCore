package bryanthedragon.morph.network.client;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.network.common.PacketSettings;
import net.minecraft.client.entity.LocalPlayer;

public class ClientHandlerSettings extends ClientMessageHandler<PacketSettings>
{
    @Override
    public void run(LocalPlayer player, PacketSettings message)
    {
        MorphManager.INSTANCE.setActiveSettings(message.settings);
    }
}