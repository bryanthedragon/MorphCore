package bryanthedragon.morph.config;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.network.morphlib.Dispatcher;
import bryanthedragon.morph.network.morphlib.common.PacketConfig;
import net.minecraft.entity.player.PlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ConfigHandler
{
    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        MinecraftServer server = event.player.getServer();

        if (server == null || server.isSinglePlayer() || !(event.player instanceof PlayerMP))
        {
            return;
        }

        for (Config config : MorphLib.proxy.configs.modules.values())
        {
            if (config.hasSyncable())
            {
                Dispatcher.sendTo(new PacketConfig(config.filterSyncable(), true), (PlayerMP) event.player);
            }
        }
    }
}
