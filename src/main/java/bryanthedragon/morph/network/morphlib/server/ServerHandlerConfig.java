package bryanthedragon.morph.network.morphlib.server;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.config.Config;
import bryanthedragon.morph.config.ConfigManager;
import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.network.morphlib.common.PacketConfig;
import bryanthedragon.morph.utils.OpHelper;
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
                ConfigManager.synchronizeConfig(present.filterSyncable(), player.getServer(), null);
            }
        }
    }
}