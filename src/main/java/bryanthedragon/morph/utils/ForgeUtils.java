package bryanthedragon.morph.utils;

import net.minecraft.entity.player.PlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

/**
 * A Utils class that holds common Forge calls.
 * Should help in abstraction for porting.
 */
public class ForgeUtils
{
    /**
     * @return a list of players on the server instance
     */
    public static List<PlayerMP> getServerPlayers()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
    }
}
