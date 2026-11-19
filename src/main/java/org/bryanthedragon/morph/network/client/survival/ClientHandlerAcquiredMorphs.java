package org.bryanthedragon.morph.network.client.survival;

import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.common.survival.PacketAcquiredMorphs;

import net.minecraft.client.player.LocalPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHandlerAcquiredMorphs extends ClientMessageHandler<PacketAcquiredMorphs>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketAcquiredMorphs message)
    {
        IMorphing morphing = Morphing.get(player);

        morphing.setAcquiredMorphs(message.morphs);
    }
}