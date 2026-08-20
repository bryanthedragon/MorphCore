package bryanthedragon.morph.network.client.survival;

import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketKeybind;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ClientHandlerKeybind extends ClientMessageHandler<PacketKeybind>

    @OnlyIn(Dist.CLIENT)
    public void run(Player player, PacketKeybind message)
    {
        Morphing.get(player).keybind(message.index, message.keybind);
    }
}