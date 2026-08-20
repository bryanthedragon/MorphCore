package bryanthedragon.morph.network;


import org.spongepowered.asm.mixin.MixinEnvironment.Side;

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
            register(PacketAction.class, ServerHandlerAction.class, Side.SERVER);

            /* Morphing */
            register(PacketMorph.class, ClientHandlerMorph.class, Side.CLIENT);
            register(PacketMorph.class, ServerHandlerMorph.class, Side.SERVER);
            register(PacketMorphPlayer.class, ClientHandlerMorphPlayer.class, Side.CLIENT);

            register(PacketAcquireMorph.class, ClientHandlerAcquireMorph.class, Side.CLIENT);
            register(PacketAcquireMorph.class, ServerHandlerAcquireMorph.class, Side.SERVER);
            register(PacketAcquiredMorphs.class, ClientHandlerAcquiredMorphs.class, Side.CLIENT);
            register(PacketSyncMorph.class, ServerHandlerSyncMorph.class, Side.SERVER);

            register(PacketSelectMorph.class, ServerHandlerSelectMorph.class, Side.SERVER);
            register(PacketClearAcquired.class, ServerHandlerClearAcquired.class, Side.SERVER);

            /* Morph state */
            register(PacketMorphState.class, ClientHandlerMorphState.class, Side.CLIENT);

            /* Managing morphs */
            register(PacketFavorite.class, ClientHandlerFavorite.class, Side.CLIENT);
            register(PacketFavorite.class, ServerHandlerFavorite.class, Side.SERVER);

            register(PacketKeybind.class, ClientHandlerKeybind.class, Side.CLIENT);
            register(PacketKeybind.class, ServerHandlerKeybind.class, Side.SERVER);

            register(PacketRemoveMorph.class, ClientHandlerRemoveMorph.class, Side.CLIENT);
            register(PacketRemoveMorph.class, ServerHandlerRemoveMorph.class, Side.SERVER);

            /* Syncing data */
            register(PacketBlacklist.class, ClientHandlerBlacklist.class, Side.CLIENT);
            register(PacketSettings.class, ClientHandlerSettings.class, Side.CLIENT);
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
    public static void sendTo(IMessage message, PlayerMP player)
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