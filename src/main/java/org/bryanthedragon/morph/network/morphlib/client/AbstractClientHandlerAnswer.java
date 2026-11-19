package org.bryanthedragon.morph.network.morphlib.client;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.network.AbstractDispatcher;
import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.network.morphlib.Dispatcher;
import org.bryanthedragon.morph.network.morphlib.common.IAnswerRequest;
import org.bryanthedragon.morph.network.morphlib.common.PacketAnswer;
import org.bryanthedragon.morph.utils.consumer.Consumers;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
@SuppressWarnings("rawtypes")
public abstract class AbstractClientHandlerAnswer<T extends PacketAnswer> extends ClientMessageHandler<T>
{
    protected static final Consumers<Object> CONSUMERS = new Consumers<>();
    protected static final Map<Integer, Long> TIME = new HashMap<>();
    /**
     * For logging / debugging purposes
     */
    protected static final Map<Integer, IAnswerRequest<?>> REQUESTS = new HashMap<>();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketAnswer message)
    {
        CONSUMERS.consume(message.getCallbackID(), message.getValue());
        TIME.remove(message.getCallbackID());
        REQUESTS.remove(message.getCallbackID());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START) return;

        long now = System.currentTimeMillis();

        for (Map.Entry<Integer, Long> entry : TIME.entrySet())
        {
            //5 minute timeout
            if (entry.getValue() + 5 * 60000 < now)
            {
                IAnswerRequest<?> request = REQUESTS.get(entry.getKey());

                MorphLib.LOGGER.info("Timeout for the answer request " + request.getClass().getSimpleName() + ". The consumer has been removed.");

                CONSUMERS.remove(entry.getKey());
                TIME.remove(entry.getKey());
                REQUESTS.remove(entry.getKey());
            }
        }
    }

    /**
     * This will register the consumer and set the resulting callbackID to the provided AnswerRequest.
     * The AnswerRequest will then be sent to the server.
     * @param request
     * @param callback
     */
    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public static <T extends Serializable> void requestServerAnswer(AbstractDispatcher dispatcher, IAnswerRequest<T> request, Consumer<T> callback)
    {
        int id = CONSUMERS.register((obj) ->
        {
            T param;

            try
            {
                param = (T) obj;
            }
            catch (ClassCastException e)
            {
                MorphLib.LOGGER.error("Type of the answer's value is incompatible with the consumer generic type!");
                e.printStackTrace();

                return;
            }

            callback.accept(param);
        });

        TIME.put(id, System.currentTimeMillis());
        REQUESTS.put(id, request);
        request.setCallbackID(id);

        dispatcher.sendToServer(request);
    }


    /**
     * Send the answer to the player. The answer's generic datatype needs to be equal
     * to the Consumer input datatype that has been registered on the client side.
     * @param receiver
     * @param answer
     * @param <T> the type of the registered Consumer input datatype.
     */
    public static <T extends Serializable> void sendAnswerTo(Player receiver, PacketAnswer<T> answer)
    {
        Dispatcher.sendTo(answer, (ServerPlayer) receiver);
    }
}
