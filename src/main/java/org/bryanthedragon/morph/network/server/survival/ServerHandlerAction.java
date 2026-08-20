package bryanthedragon.morph.network.server.survival;


import bryanthedragon.morph.api.events.MorphActionEvent;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketAction;
import bryanthedragon.morph.network.server.message.ServerMessageHandler;

import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.common.MinecraftForge;

public class ServerHandlerAction extends ServerMessageHandler<PacketAction>
{
    @Override
    public void run(ServerPlayer player, PacketAction message)
    {
        IMorphing capability = Morphing.get(player);

        if (capability != null && capability.isMorphed() && !player.isSpectator())
        {
            AbstractMorph morph = capability.getCurrentMorph();

            morph.action(player);
            MinecraftForge.EVENT_BUS.post(new MorphActionEvent(player, morph.getSettings().action, morph));
        }
    }
}