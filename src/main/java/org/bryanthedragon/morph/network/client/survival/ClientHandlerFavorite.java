package org.bryanthedragon.morph.network.client.survival;

import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.common.survival.PacketFavorite;

import net.minecraft.client.player.LocalPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHandlerFavorite extends ClientMessageHandler<PacketFavorite>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketFavorite message)
    {
        Morphing.get(player).favorite(message.index);
    }
}