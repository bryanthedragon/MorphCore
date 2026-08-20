package bryanthedragon.morph.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityUtils
{
    @OnlyIn(Dist.CLIENT)
    public static GameType getGameMode()
    {
        return getGameMode(Minecraft.getInstance().player);
    }

    @OnlyIn(Dist.CLIENT)
    public static GameType getGameMode(Player player)
    {
        PlayerInfo networkPlayerInfo = EntityUtils.getNetworkInfo(player);

        return networkPlayerInfo == null ? GameType.SURVIVAL : networkPlayerInfo.getGameMode();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isAdventureMode(Player player)
    {
        PlayerInfo info = getNetworkInfo(player);

        return info != null && info.getGameMode() == GameType.ADVENTURE;
    }

    @OnlyIn(Dist.CLIENT)
    public static PlayerInfo getNetworkInfo(Player player)
    {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();

        if (connection == null)
        {
            return null;
        }

        return connection.getPlayerInfo(player.getGameProfile().getId());
    }
}
