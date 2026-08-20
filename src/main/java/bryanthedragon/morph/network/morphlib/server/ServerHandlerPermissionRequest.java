package bryanthedragon.morph.network.morphlib.server;

import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.network.morphlib.client.ClientHandlerAnswer;
import bryanthedragon.morph.network.morphlib.common.PacketRequestPermission;
import bryanthedragon.morph.permissions.PermissionCategory;
import net.minecraft.entity.player.ServerPlayer;

public class ServerHandlerPermissionRequest extends ServerMessageHandler<PacketRequestPermission>
{
    @Override
    public void run(ServerPlayer player, PacketRequestPermission message)
    {
        PermissionCategory perm = message.getPermissionRequest();

        boolean hasPermission = perm != null && perm.playerHasPermission(player);

        ClientHandlerAnswer.sendAnswerTo(player, message.getAnswer(hasPermission));
    }
}
