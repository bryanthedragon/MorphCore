package bryanthedragon.morph.network.morphlib;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.network.AbstractDispatcher;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.player.PlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(MorphLib.MOD_ID)
    {
        @Override
        public void register()
        {
            register(PacketDropItem.class, ServerHandlerDropItem.class, Side.SERVER);

            /* Config related packets */
            register(PacketRequestConfigs.class, ServerHandlerRequestConfigs.class, Side.SERVER);
            register(PacketConfig.class, ServerHandlerConfig.class, Side.SERVER);
            register(PacketConfig.class, ClientHandlerConfig.class, Side.CLIENT);

            //TODO abstract confirm thing into server to client to server answer thing - see IAnswerRequest etc.
            /* Confirm related packets */
            register(PacketConfirm.class, ClientHandlerConfirm.class, Side.CLIENT);
            register(PacketConfirm.class, ServerHandlerConfirm.class, Side.SERVER);

            /* client answer related packets */
            register(PacketAnswer.class, ClientHandlerAnswer.class, Side.CLIENT);
            register(PacketBoolean.class, ClientHandlerBoolean.class, Side.CLIENT);

            register(PacketRequestPermission.class, ServerHandlerPermissionRequest.class, Side.SERVER);
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