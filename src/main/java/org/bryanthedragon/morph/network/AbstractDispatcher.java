package org.bryanthedragon.morph.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Network dispatcher
 *
 * Wraps Forge's {@link SimpleChannel} while keeping the old SimpleImpl-style
 * API (one message class may have both a client and a server handler).
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractDispatcher
{
    private static final String PROTOCOL_VERSION = "1";

    private final SimpleChannel dispatcher;
    private final Map<Class<? extends IMessage>, Handlers<?>> handlers = new HashMap<>();
    private int nextPacketID;

    public AbstractDispatcher(String modID)
    {
        this.dispatcher = NetworkRegistry.newSimpleChannel(new ResourceLocation(modID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    }

    public SimpleChannel get()
    {
        return this.dispatcher;
    }

    /**
     * Here you supposed to register packets to handlers 
     */
    public abstract void register();

    /**
     * Send message to players who are tracking given entity
     */
    public void sendToTracked(Entity entity, IMessage message)
    {
        this.dispatcher.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    /**
     * Send message to given player
     */
    public void sendTo(IMessage message, ServerPlayer player)
    {
        this.dispatcher.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    /**
     * Send message to all players
     */
    public void sendToAll(IMessage message)
    {
        this.dispatcher.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Send message to all players around the given point
     */
    public void sendToAllAround(IMessage message, PacketDistributor.TargetPoint point)
    {
        this.dispatcher.send(PacketDistributor.NEAR.with(() -> point), message);
    }

    /**
     * Send message to the server
     */
    public void sendToServer(IMessage message)
    {
        this.dispatcher.sendToServer(message);
    }

    /**
     * Register given message with given message handler on a given side
     */
    public <REQ extends IMessage> void register(Class<REQ> message, Class<? extends AbstractMessageHandler<REQ>> handler, Dist side) 
    { 
        AbstractMessageHandler<REQ> instance;
        try
        {
            instance = (AbstractMessageHandler<REQ>) handler.getDeclaredConstructor().newInstance();
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException("Failed to instantiate message handler " + handler.getName(), e);
        }

        @SuppressWarnings("unchecked")
        Handlers<REQ> pair = (Handlers<REQ>) this.handlers.get(message);

        if (pair == null)
        {
            pair = new Handlers<>();
            this.handlers.put(message, pair);
            this.registerMessage(message, pair);
        }

        if (side == Dist.CLIENT)
        {
            pair.client = instance;
        }
        else
        {
            pair.server = instance;
        }
    }

    private <REQ extends IMessage> void registerMessage(Class<REQ> message, Handlers<REQ> pair)
    {
        this.dispatcher.registerMessage(this.nextPacketID++, message, (msg, buf) -> msg.toBytes(buf), (buf) ->
        {
            try
            {
                REQ msg = message.getDeclaredConstructor().newInstance();

                msg.fromBytes(buf);

                return msg;
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException("Failed to decode message " + message.getName(), e);
            }
        }, (msg, ctxSupplier) ->
        {
            NetworkEvent.Context ctx = ctxSupplier.get();
            AbstractMessageHandler<REQ> handler = ctx.getDirection().getReceptionSide().isClient() ? pair.client : pair.server;

            if (handler != null)
            {
                IMessage reply = handler.onMessage(msg, ctx);

                if (reply != null)
                {
                    this.dispatcher.reply(reply, ctx);
                }
            }

            ctx.setPacketHandled(true);
        });
    }

    private static class Handlers<T extends IMessage>
    {
        public AbstractMessageHandler<T> client;
        public AbstractMessageHandler<T> server;
    }
}
