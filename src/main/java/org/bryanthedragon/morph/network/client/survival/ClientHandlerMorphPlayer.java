package bryanthedragon.morph.network.client.survival;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.MorphingProvider;
import bryanthedragon.morph.network.common.survival.PacketMorphPlayer;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHandlerMorphPlayer extends ClientMessageHandler<PacketMorphPlayer>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketMorphPlayer message)
    {
        Entity entity = player.level().getEntity(message.id);
        if (entity == null)
        {
            return;
        }

        IMorphing capability = entity.getCapability(MorphingProvider.MORPHING_CAP, null).orElse(null);

        if (capability != null)
        {
            capability.setCurrentMorph(message.morph, (Player) entity, true);
        }
    }
}
