package bryanthedragon.morph.network.client.survival;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketKeybind;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ClientHandlerKeybind extends ClientMessageHandler<PacketKeybind>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketKeybind message)
    {
        Morphing.get(player).keybind(message.index, message.keybind);
    }
}
