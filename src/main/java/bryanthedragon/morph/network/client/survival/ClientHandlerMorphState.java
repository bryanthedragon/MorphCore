package bryanthedragon.morph.network.client.survival;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.EntityMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketMorphState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHandlerMorphState extends ClientMessageHandler<PacketMorphState>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketMorphState message)
    {
        IMorphing capability = Morphing.get(player);
        AbstractMorph morph = capability.getCurrentMorph();

        if (morph instanceof EntityMorph)
        {
            Entity entity = ((EntityMorph) morph).getEntity(player.level());

            entity.setId(message.entityID);
        }

        capability.setHasSquidAir(message.hasSquidAir);
        capability.setSquidAir(message.squidAir);
    }
}
