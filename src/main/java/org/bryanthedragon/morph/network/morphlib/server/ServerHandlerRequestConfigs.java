package org.bryanthedragon.morph.network.morphlib.server;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.config.Config;
import org.bryanthedragon.morph.config.ConfigManager;
import org.bryanthedragon.morph.network.server.message.ServerMessageHandler;
import org.bryanthedragon.morph.utils.helpers.op.OpHelper;
import org.bryanthedragon.morph.network.morphlib.Dispatcher;
import org.bryanthedragon.morph.network.morphlib.common.PacketConfig;
import org.bryanthedragon.morph.network.morphlib.common.PacketRequestConfigs;
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