package bryanthedragon.morph.network.server.creative;

import bryanthedragon.morph.network.server.message.ServerMessageHandler;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.network.common.creative.PacketMorph;

import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerMorph extends ServerMessageHandler<PacketMorph>
{
    @Override
    public void run(ServerPlayer player, PacketMorph message)
    {
        if (MorphCore.proxy.canUse(player))
        {
            MorphAPI.morph(player, message.morph, MorphCore.allowMorphingIntoCategoryMorphs.get());
        }
    }
}