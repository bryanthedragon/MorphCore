package bryanthedragon.morph.network.morphlib;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.network.AbstractDispatcher;
import bryanthedragon.morph.network.IMessage;
import bryanthedragon.morph.network.morphlib.client.ClientHandlerAnswer;
import bryanthedragon.morph.network.morphlib.client.ClientHandlerBoolean;
import bryanthedragon.morph.network.morphlib.client.ClientHandlerConfig;
import bryanthedragon.morph.network.morphlib.client.ClientHandlerConfirm;
import bryanthedragon.morph.network.morphlib.common.PacketAnswer;
import bryanthedragon.morph.network.morphlib.common.PacketBoolean;
import bryanthedragon.morph.network.morphlib.common.PacketConfig;
import bryanthedragon.morph.network.morphlib.common.PacketConfirm;
import bryanthedragon.morph.network.morphlib.common.PacketDropItem;
import bryanthedragon.morph.network.morphlib.common.PacketRequestConfigs;
import bryanthedragon.morph.network.morphlib.common.PacketRequestPermission;
import bryanthedragon.morph.network.morphlib.server.ServerHandlerConfig;
import bryanthedragon.morph.network.morphlib.server.ServerHandlerConfirm;
import bryanthedragon.morph.network.morphlib.server.ServerHandlerDropItem;
import bryanthedragon.morph.network.morphlib.server.ServerHandlerPermissionRequest;
import bryanthedragon.morph.network.morphlib.server.ServerHandlerRequestConfigs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.api.distmarker.Dist;

public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(MorphLib.MOD_ID)
    {
        @Override
        public void register()
        {
            register(PacketDropItem.class, ServerHandlerDropItem.class, Dist.DEDICATED_SERVER);

            /* Config related packets */
            register(PacketRequestConfigs.class, ServerHandlerRequestConfigs.class, Dist.DEDICATED_SERVER);
            register(PacketConfig.class, ServerHandlerConfig.class, Dist.DEDICATED_SERVER);
            register(PacketConfig.class, ClientHandlerConfig.class, Dist.CLIENT);

            /* Confirm related packets */
            register(PacketConfirm.class, ClientHandlerConfirm.class, Dist.CLIENT);
            register(PacketConfirm.class, ServerHandlerConfirm.class, Dist.DEDICATED_SERVER);

            /* client answer related packets */
            register(PacketAnswer.class, ClientHandlerAnswer.class, Dist.CLIENT);
            register(PacketBoolean.class, ClientHandlerBoolean.class, Dist.CLIENT);

            register(PacketRequestPermission.class, ServerHandlerPermissionRequest.class, Dist.DEDICATED_SERVER);
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