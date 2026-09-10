package bryanthedragon.morph.network;


import net.minecraftforge.api.distmarker.Dist;

import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.network.AbstractDispatcher;
import bryanthedragon.morph.network.client.creative.ClientHandlerAcquireMorph;
import bryanthedragon.morph.network.client.survival.ClientHandlerAcquiredMorphs;
import bryanthedragon.morph.network.client.ClientHandlerBlacklist;
import bryanthedragon.morph.network.client.survival.ClientHandlerFavorite;
import bryanthedragon.morph.network.client.survival.ClientHandlerKeybind;
import bryanthedragon.morph.network.client.creative.ClientHandlerMorph;
import bryanthedragon.morph.network.client.survival.ClientHandlerMorphPlayer;
import bryanthedragon.morph.network.client.survival.ClientHandlerMorphState;
import bryanthedragon.morph.network.client.survival.ClientHandlerRemoveMorph;
import bryanthedragon.morph.network.client.ClientHandlerSettings;
import bryanthedragon.morph.network.common.creative.PacketAcquireMorph;
import bryanthedragon.morph.network.common.creative.PacketClearAcquired;
import bryanthedragon.morph.network.common.survival.PacketAcquiredMorphs;
import bryanthedragon.morph.network.common.survival.PacketAction;
import bryanthedragon.morph.network.common.PacketBlacklist;
import bryanthedragon.morph.network.common.survival.PacketFavorite;
import bryanthedragon.morph.network.common.survival.PacketKeybind;
import bryanthedragon.morph.network.common.creative.PacketMorph;
import bryanthedragon.morph.network.common.survival.PacketMorphPlayer;
import bryanthedragon.morph.network.common.survival.PacketMorphState;
import bryanthedragon.morph.network.common.survival.PacketRemoveMorph;
import bryanthedragon.morph.network.common.survival.PacketSelectMorph;
import bryanthedragon.morph.network.common.PacketSettings;
import bryanthedragon.morph.network.common.creative.PacketSyncMorph;
import bryanthedragon.morph.network.server.creative.ServerHandlerAcquireMorph;
import bryanthedragon.morph.network.server.creative.ServerHandlerClearAcquired;
import bryanthedragon.morph.network.server.survival.ServerHandlerAction;
import bryanthedragon.morph.network.server.survival.ServerHandlerFavorite;
import bryanthedragon.morph.network.server.survival.ServerHandlerKeybind;
import bryanthedragon.morph.network.server.creative.ServerHandlerMorph;
import bryanthedragon.morph.network.server.survival.ServerHandlerRemoveMorph;
import bryanthedragon.morph.network.server.survival.ServerHandlerSelectMorph;
import bryanthedragon.morph.network.server.creative.ServerHandlerSyncMorph;
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