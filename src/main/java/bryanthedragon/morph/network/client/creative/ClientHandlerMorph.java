package bryanthedragon.morph.network.client.creative;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.creative.PacketMorph;
import net.minecraft.client.entity.PlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerMorph extends ClientMessageHandler<PacketMorph>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(PlayerSP player, PacketMorph message)
    {
        IMorphing capability = Morphing.get(player);

        if (capability != null)
        {
            capability.setCurrentMorph(message.morph, player, true);
        }
    }
}