package bryanthedragon.morph.network.client.survival;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.EntityMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketMorphState;
import net.minecraft.client.entity.PlayerSP;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerMorphState extends ClientMessageHandler<PacketMorphState>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(PlayerSP player, PacketMorphState message)
    {
        IMorphing capability = Morphing.get(player);
        AbstractMorph morph = capability.getCurrentMorph();

        if (morph instanceof EntityMorph)
        {
            Entity entity = ((EntityMorph) morph).getEntity(player.world);

            entity.setEntityId(message.entityID);
        }

        capability.setHasSquidAir(message.hasSquidAir);
        capability.setSquidAir(message.squidAir);
    }
}
