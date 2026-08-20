package bryanthedragon.morph.network.morphlib.server;

import bryanthedragon.morph.network.server.message.ServerMessageHandler;
import bryanthedragon.morph.network.morphlib.common.PacketConfirm;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;

public class ServerHandlerConfirm extends ServerMessageHandler<PacketConfirm>
{
    private static TreeMap<Integer, Consumer<Boolean>> consumers = new TreeMap<Integer, Consumer<Boolean>>();

    @Override
    public void run(ServerPlayer ServerPlayer, PacketConfirm packetConfirm)
    {
        if (consumers.containsKey(packetConfirm.consumerID))
        {
            consumers.remove(packetConfirm.consumerID).accept(packetConfirm.confirm);
        }
    }

    public static void addConsumer(int id, Consumer<Boolean> item)
    {
        consumers.put(id, item);
    }

    public static Entry<Integer, Consumer<Boolean>> getLastConsumerEntry()
    {
        return consumers.lastEntry();
    }
}
