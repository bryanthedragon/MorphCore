package bryanthedragon.morph.network.server.creative;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.network.common.creative.PacketMorph;
import net.minecraft.entity.player.PlayerMP;

public class ServerHandlerMorph extends ServerMessageHandler<PacketMorph>
{
    @Override
    public void run(PlayerMP player, PacketMorph message)
    {
        if (MorphCore.proxy.canUse(player))
        {
            MorphAPI.morph(player, message.morph, MorphCore.allowMorphingIntoCategoryMorphs.get());
        }
    }
}