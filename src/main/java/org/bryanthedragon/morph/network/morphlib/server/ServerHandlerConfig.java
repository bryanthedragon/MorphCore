package org.bryanthedragon.morph.network.morphlib.server;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.config.Config;
import org.bryanthedragon.morph.config.ConfigManager;
import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;
import org.bryanthedragon.morph.utils.helpers.op.OpHelper;
import org.bryanthedragon.morph.network.morphlib.common.PacketConfig;

import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerConfig extends ServerMessageHandler<PacketConfig>
{
    @Override
    public void run(ServerPlayer player, PacketConfig message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        Config present = MorphLib.proxy.configs.modules.get(message.config.id);

        if (present != null)
        {
            present.copy(message.config);
            present.save();

            if (present.hasSyncable())
            {
                ConfigManager.synchronizeConfig(present.filterSyncable(), player.serverLevel().getServer(), null);
            }
        }
    }
}