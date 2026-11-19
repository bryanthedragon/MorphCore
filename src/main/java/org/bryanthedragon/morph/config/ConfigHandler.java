package org.bryanthedragon.morph.config;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.network.morphlib.Dispatcher;
import org.bryanthedragon.morph.network.morphlib.common.PacketConfig;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ConfigHandler
{
    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        MinecraftServer server = event.getEntity().getServer();

        if (server == null || server.isSingleplayer() || !(event.getEntity() instanceof ServerPlayer))
        {
            return;
        }

        for (Config config : MorphLib.proxy.configs.modules.values())
        {
            if (config.hasSyncable())
            {
                Dispatcher.sendTo(new PacketConfig(config.filterSyncable(), true), (ServerPlayer) event.getEntity());
            }
        }
    }
}
