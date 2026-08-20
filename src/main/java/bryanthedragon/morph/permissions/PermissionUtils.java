package bryanthedragon.morph.permissions;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.morph.network.morphlib.client.ClientHandlerAnswer;
import bryanthedragon.morph.network.morphlib.common.PacketRequestPermission;

public class PermissionUtils
{
    /**
     * Side independent method for checking if it has the permission and then passing the result to the callback
     * @param node the full name of the permission
     * @param player the player to check for the permission
     * @param callback the callback to be executed after checking the player
     *                 or after the server has sent the permission result.
     */
    public static void hasPermission(Player player, PermissionCategory permission, Consumer<Boolean> callback)
    {
        if (!Minecraft.getInstance().world.isRemote)
        {
            callback.accept(permission.playerHasPermission(player));
        }
        else
        {
            //in singleplayer there is no use in having a permission system
            if (Minecraft.getInstance().isIntegratedServerRunning())
            {
                callback.accept(true);
            }
            else
            {
                ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER, new PacketRequestPermission(-1, permission), callback);
            }
        }
    }
}
