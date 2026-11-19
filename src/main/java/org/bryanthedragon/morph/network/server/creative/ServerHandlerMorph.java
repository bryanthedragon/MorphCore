package org.bryanthedragon.morph.network.server.creative;

import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;
import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.api.MorphAPI;
import org.bryanthedragon.morph.network.common.creative.PacketMorph;

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