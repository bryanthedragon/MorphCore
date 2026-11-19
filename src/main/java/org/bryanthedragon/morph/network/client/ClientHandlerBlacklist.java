package org.bryanthedragon.morph.network.client;

import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.network.common.PacketBlacklist;

import net.minecraft.client.player.LocalPlayer;

/**
 * Client handler blacklist loader 
 */
public class ClientHandlerBlacklist extends ClientMessageHandler<PacketBlacklist>
{
    @Override
    public void run(LocalPlayer player, PacketBlacklist message)
    {
        MorphManager.INSTANCE.setActiveBlacklist(player.level(), message.blacklist);
    }
}
