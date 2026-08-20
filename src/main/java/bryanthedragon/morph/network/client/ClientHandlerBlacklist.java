package bryanthedragon.morph.network.client;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.network.common.PacketBlacklist;
import net.minecraft.client.entity.LocalPlayer;

/**
 * Client handler blacklist loader 
 */
public class ClientHandlerBlacklist extends ClientMessageHandler<PacketBlacklist>
{
    @Override
    public void run(LocalPlayer player, PacketBlacklist message)
    {
        MorphManager.INSTANCE.setActiveBlacklist(player.world, message.blacklist);
    }
}
