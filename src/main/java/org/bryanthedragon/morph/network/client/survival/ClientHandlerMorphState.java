package org.bryanthedragon.morph.network.client.survival;

import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.api.morphs.main.EntityMorph;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.network.common.survival.PacketMorphState;

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
