package bryanthedragon.morph.network.client.survival;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.MorphingProvider;
import bryanthedragon.morph.network.common.survival.PacketMorphPlayer;
import net.minecraft.client.entity.PlayerSP;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerMorphPlayer extends ClientMessageHandler<PacketMorphPlayer>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(PlayerSP player, PacketMorphPlayer message)
    {
        Entity entity = player.world.getEntityByID(message.id);
        IMorphing capability = entity.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability != null)
        {
            capability.setCurrentMorph(message.morph, (Player) entity, true);
        }
    }
}
