package bryanthedragon.morph.network.server.survival;

import java.util.List;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketSelectMorph;
import net.minecraft.entity.player.PlayerMP;
import net.minecraft.world.GameType;

public class ServerHandlerSelectMorph extends ServerMessageHandler<PacketSelectMorph>
{
    @Override
    public void run(PlayerMP player, PacketSelectMorph message)
    {
        if (player.interactionManager.getGameType() == GameType.ADVENTURE)
        {
            return;
        }

        int index = message.index;

        IMorphing capability = Morphing.get(player);
        List<AbstractMorph> morphs = capability.getAcquiredMorphs();
        AbstractMorph morph = null;

        if (!morphs.isEmpty() && index >= 0 && index < morphs.size())
        {
            morph = morphs.get(index);
        }

        MorphAPI.morph(player, MorphUtils.copy(morph), false);
    }
}