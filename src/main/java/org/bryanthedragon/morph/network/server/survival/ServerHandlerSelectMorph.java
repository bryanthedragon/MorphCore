package bryanthedragon.morph.network.server.survival;

import java.util.List;

import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.utils.morph.MorphUtils;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketSelectMorph;
import bryanthedragon.morph.network.server.message.ServerMessageHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ServerHandlerSelectMorph extends ServerMessageHandler<PacketSelectMorph>
{
    @Override
    public void run(ServerPlayer player, PacketSelectMorph message)
    {
        if (player.gameMode.getGameModeForPlayer() == GameType.ADVENTURE)
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