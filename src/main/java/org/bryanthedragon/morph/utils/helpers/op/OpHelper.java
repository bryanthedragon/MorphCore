package org.bryanthedragon.morph.utils.helpers.op;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OpHelper
{
    /**
     * Minimum OP level according to vanilla code
     */
    public static final int VANILLA_OP_LEVEL = 2;

    @OnlyIn(Dist.CLIENT)
    public static int getPlayerOpLevel()
    {
        LocalPlayer player = Minecraft.getInstance().player;

        return player == null ? 0 : player.getPermissionLevel();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isPlayerOp()
    {
        return isOp(getPlayerOpLevel());
    }

    public static boolean isPlayerOp(ServerPlayer player)
    {
        if (player == null)
        {
            return false;
        }

        MinecraftServer server = player.getServer();

        if (server.getPlayerList().isOp(player.getGameProfile()))
        {
            ServerOpListEntry userEntry = server.getPlayerList().getOps().get(player.getGameProfile());

            return isOp(userEntry == null ? server.getOperatorUserPermissionLevel() : userEntry.getLevel());
        }

        return false;
    }

    public static boolean isOp(int opLevel)
    {
        return opLevel >= VANILLA_OP_LEVEL;
    }
}
