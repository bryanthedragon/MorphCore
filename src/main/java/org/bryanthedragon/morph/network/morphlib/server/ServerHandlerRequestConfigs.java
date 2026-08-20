package bryanthedragon.morph.network.morphlib.server;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.config.Config;
import bryanthedragon.morph.config.ConfigManager;
import bryanthedragon.morph.network.server.message.ServerMessageHandler;
import bryanthedragon.morph.utils.helpers.op.OpHelper;
import bryanthedragon.morph.network.morphlib.Dispatcher;
import bryanthedragon.morph.network.morphlib.common.PacketConfig;
import bryanthedragon.morph.network.morphlib.common.PacketRequestConfigs;
import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerRequestConfigs extends ServerMessageHandler<PacketRequestConfigs>
{
    @Override
    public void run(ServerPlayer player, PacketRequestConfigs message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        ConfigManager manager = MorphLib.proxy.configs;

        for (Config config : manager.modules.values())
        {
            Config serverSide = config.filterServerSide();

            if (!serverSide.values.isEmpty())
            {
                Dispatcher.sendTo(new PacketConfig(serverSide), player);
            }
        }
    }
}