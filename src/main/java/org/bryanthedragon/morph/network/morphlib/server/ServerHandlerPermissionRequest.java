package org.bryanthedragon.morph.network.morphlib.server;

import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;
import org.bryanthedragon.morph.network.morphlib.client.ClientHandlerAnswer;
import org.bryanthedragon.morph.network.morphlib.common.PacketRequestPermission;
import org.bryanthedragon.morph.permissions.category.PermissionCategory;

import net.minecraft.server.level.ServerPlayer;

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
