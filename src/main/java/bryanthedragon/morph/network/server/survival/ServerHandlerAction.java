package bryanthedragon.morph.network.server.survival;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.api.events.MorphActionEvent;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketAction;
import net.minecraft.entity.player.ServerPlayer;
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