package org.bryanthedragon.morph.network;


import net.minecraftforge.api.distmarker.Dist;

import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.network.client.creative.ClientHandlerAcquireMorph;
import org.bryanthedragon.morph.network.client.survival.ClientHandlerAcquiredMorphs;
import org.bryanthedragon.morph.network.client.ClientHandlerBlacklist;
import org.bryanthedragon.morph.network.client.survival.ClientHandlerFavorite;
import org.bryanthedragon.morph.network.client.survival.ClientHandlerKeybind;
import org.bryanthedragon.morph.network.client.creative.ClientHandlerMorph;
import org.bryanthedragon.morph.network.client.survival.ClientHandlerMorphPlayer;
import org.bryanthedragon.morph.network.client.survival.ClientHandlerMorphState;
import org.bryanthedragon.morph.network.client.survival.ClientHandlerRemoveMorph;
import org.bryanthedragon.morph.network.client.ClientHandlerSettings;
import org.bryanthedragon.morph.network.common.creative.PacketAcquireMorph;
import org.bryanthedragon.morph.network.common.creative.PacketClearAcquired;
import org.bryanthedragon.morph.network.common.survival.PacketAcquiredMorphs;
import org.bryanthedragon.morph.network.common.survival.PacketAction;
import org.bryanthedragon.morph.network.common.PacketBlacklist;
import org.bryanthedragon.morph.network.common.survival.PacketFavorite;
import org.bryanthedragon.morph.network.common.survival.PacketKeybind;
import org.bryanthedragon.morph.network.common.creative.PacketMorph;
import org.bryanthedragon.morph.network.common.survival.PacketMorphPlayer;
import org.bryanthedragon.morph.network.common.survival.PacketMorphState;
import org.bryanthedragon.morph.network.common.survival.PacketRemoveMorph;
import org.bryanthedragon.morph.network.common.survival.PacketSelectMorph;
import org.bryanthedragon.morph.network.common.PacketSettings;
import org.bryanthedragon.morph.network.common.creative.PacketSyncMorph;
import org.bryanthedragon.morph.network.server.creative.ServerHandlerAcquireMorph;
import org.bryanthedragon.morph.network.server.creative.ServerHandlerClearAcquired;
import org.bryanthedragon.morph.network.server.survival.ServerHandlerAction;
import org.bryanthedragon.morph.network.server.survival.ServerHandlerFavorite;
import org.bryanthedragon.morph.network.server.survival.ServerHandlerKeybind;
import org.bryanthedragon.morph.network.server.creative.ServerHandlerMorph;
import org.bryanthedragon.morph.network.server.survival.ServerHandlerRemoveMorph;
import org.bryanthedragon.morph.network.server.survival.ServerHandlerSelectMorph;
import org.bryanthedragon.morph.network.server.creative.ServerHandlerSyncMorph;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Network dispatcher
 */
public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(MorphCore.MOD_ID)
    {
        @Override
        public void register()
        {
            /* Action */
            register(PacketAction.class, ServerHandlerAction.class, Dist.DEDICATED_SERVER);

            /* Morphing */
            register(PacketMorph.class, ClientHandlerMorph.class, Dist.CLIENT);
            register(PacketMorph.class, ServerHandlerMorph.class, Dist.DEDICATED_SERVER);
            register(PacketMorphPlayer.class, ClientHandlerMorphPlayer.class, Dist.CLIENT);

            register(PacketAcquireMorph.class, ClientHandlerAcquireMorph.class, Dist.CLIENT);
            register(PacketAcquireMorph.class, ServerHandlerAcquireMorph.class, Dist.DEDICATED_SERVER);
            register(PacketAcquiredMorphs.class, ClientHandlerAcquiredMorphs.class, Dist.CLIENT);
            register(PacketSyncMorph.class, ServerHandlerSyncMorph.class, Dist.DEDICATED_SERVER);

            register(PacketSelectMorph.class, ServerHandlerSelectMorph.class, Dist.DEDICATED_SERVER);
            register(PacketClearAcquired.class, ServerHandlerClearAcquired.class, Dist.DEDICATED_SERVER);

            /* Morph state */
            register(PacketMorphState.class, ClientHandlerMorphState.class, Dist.CLIENT);

            /* Managing morphs */
            register(PacketFavorite.class, ClientHandlerFavorite.class, Dist.CLIENT);
            register(PacketFavorite.class, ServerHandlerFavorite.class, Dist.DEDICATED_SERVER);

            register(PacketKeybind.class, ClientHandlerKeybind.class, Dist.CLIENT);
            register(PacketKeybind.class, ServerHandlerKeybind.class, Dist.DEDICATED_SERVER);

            register(PacketRemoveMorph.class, ClientHandlerRemoveMorph.class, Dist.CLIENT);
            register(PacketRemoveMorph.class, ServerHandlerRemoveMorph.class, Dist.DEDICATED_SERVER);

            /* Syncing data */
            register(PacketBlacklist.class, ClientHandlerBlacklist.class, Dist.CLIENT);
            register(PacketSettings.class, ClientHandlerSettings.class, Dist.CLIENT);
        }
    };

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message)
    {
        DISPATCHER.sendToTracked(entity, message);
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, ServerPlayer player)
    {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        DISPATCHER.register();
    }
}