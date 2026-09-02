package bryanthedragon.morph.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Network dispatcher
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractDispatcher
{
    public static final String PROTOCOL_VERSION = "1";

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
     * @param message
     */
    public void sendToAll(IMessage message)
    {
        this.dispatcher.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Send message to all players around the given point
     * @param message
     * @param point The {@link PacketDistributor.TargetPoint} around which to send
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
     *
     * A message class may be registered twice (once per side); the channel
     * entry is created on the first registration and the handler for the other
     * side is attached on the second one.
     */
    @SuppressWarnings("unchecked")
    public <REQ extends IMessage> void register(Class<REQ> message, Class<? extends AbstractMessageHandler<REQ>> handler, LogicalSide side)
    {
        Handlers<REQ> entry = (Handlers<REQ>) this.handlers.get(message);

        if (entry == null)
        {
            entry = new Handlers<REQ>();
            this.handlers.put(message, entry);
            this.registerChannelMessage(message, entry);
        }

        entry.set(side, this.instantiate(handler));
    }

    private <REQ extends IMessage> void registerChannelMessage(Class<REQ> message, Handlers<REQ> entry)
    {
        this.dispatcher.messageBuilder(message, this.nextPacketID++)
            .encoder(IMessage::toBytes)
            .decoder((FriendlyByteBuf buf) ->
            {
                REQ instance = this.instantiate(message);

                instance.fromBytes(buf);

                return instance;
            })
            .consumerNetworkThread((REQ msg, Supplier<NetworkEvent.Context> ctx) ->
            {
                NetworkEvent.Context context = ctx.get();
                AbstractMessageHandler<REQ> target = entry.get(context.getDirection().getReceptionSide());

                if (target != null)
                {
                    IMessage reply = target.onMessage(msg, context);

                    if (reply != null)
                    {
                        this.dispatcher.reply(reply, context);
                    }
                }

                context.setPacketHandled(true);
            })
            .add();
    }

    private <T> T instantiate(Class<T> clazz)
    {
        try
        {
            return clazz.getDeclaredConstructor().newInstance();
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalArgumentException("Class " + clazz.getName() + " must have a public no-arg constructor!", e);
        }
    }

    /**
     * Per-message handlers for both logical sides
     */
    private static class Handlers<T extends IMessage>
    {
        public AbstractMessageHandler<T> client;
        public AbstractMessageHandler<T> server;

        public void set(LogicalSide side, AbstractMessageHandler<T> handler)
        {
            if (side.isClient())
            {
                this.client = handler;
            }
            else
            {
                this.server = handler;
            }
        }

        public AbstractMessageHandler<T> get(LogicalSide side)
        {
            return side.isClient() ? this.client : this.server;
        }
    }
}
