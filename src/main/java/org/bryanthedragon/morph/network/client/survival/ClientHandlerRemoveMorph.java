package org.bryanthedragon.morph.network.client.survival;

import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.common.survival.PacketRemoveMorph;

import net.minecraft.client.player.LocalPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHandlerRemoveMorph extends ClientMessageHandler<PacketRemoveMorph>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketRemoveMorph message)
    {
        Morphing.get(player).remove(message.index);
    }
}