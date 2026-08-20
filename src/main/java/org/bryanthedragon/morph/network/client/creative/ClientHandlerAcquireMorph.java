package bryanthedragon.morph.network.client.creative;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.MorphCoreProxy.ClientCoreProxy;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.creative.PacketAcquireMorph;

import net.minecraft.client.player.LocalPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHandlerAcquireMorph extends ClientMessageHandler<PacketAcquireMorph>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketAcquireMorph message)
    {
        IMorphing morphing = Morphing.get(player);

        morphing.acquireMorph(message.morph);

        ClientCoreProxy.morphOverlay.add(message.morph);
    }
}