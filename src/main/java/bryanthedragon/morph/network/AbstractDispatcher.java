package bryanthedragon.morph.network;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
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
    private static final String PROTOCOL_VERSION = "1";

    private final SimpleChannel dispatcher;
    private int nextPacketID;

    public AbstractDispatcher(String modID)
    {
        this.dispatcher = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(modID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();
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
     */
    public <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, LogicalSide side)
    {
        IMessageHandler<REQ, REPLY> instance = this.instantiate(handler);
        NetworkDirection direction = side == LogicalSide.CLIENT ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER;

        this.dispatcher.registerMessage(this.nextPacketID++, message, (msg, buffer) -> msg.toBytes(buffer), (buffer) ->
        {
            REQ msg = this.instantiate(message);

            msg.fromBytes(buffer);

            return msg;
        }, (msg, context) ->
        {
            NetworkEvent.Context ctx = context.get();

            ctx.enqueueWork(() ->
            {
                REPLY reply = instance.onMessage(msg, ctx);

                if (reply != null)
                {
                    this.dispatcher.reply(reply, ctx);
                }
            });

            ctx.setPacketHandled(true);
        }, Optional.of(direction));
    }

    private <T> T instantiate(Class<? extends T> clazz)
    {
        try
        {
            return clazz.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Class " + clazz.getName() + " must have a public no-argument constructor!", e);
        }
    }
}
