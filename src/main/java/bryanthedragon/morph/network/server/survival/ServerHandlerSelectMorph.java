package bryanthedragon.morph.network.server.survival;

import java.util.List;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.network.common.survival.PacketSelectMorph;
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