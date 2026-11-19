package org.bryanthedragon.morph.network.server.message;

import org.bryanthedragon.morph.network.AbstractMessageHandler;
import org.bryanthedragon.morph.network.IMessage;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This class passes operation from Netty to Minecraft (Server) Thread. This
 * class will prevent the client-side message handling method from appearing in
 * server message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class ServerMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>
{
    public abstract void run(final ServerPlayer player, final T message);

    @Override
    public IMessage handleServerMessage(final ServerPlayer player, final T message)
    {
        player.getServer().execute(() -> ServerMessageHandler.this.run(player, message));

        return null;
    }

    @Override
    public final IMessage handleClientMessage(final T message)
    {
        return null;
    }

    /**
     * Safe way to get a block entity on the server without exposing code
     * to ACG (Arbitrary Chunk Generation) exploit (thanks to Paul Fulham)
     */
    protected BlockEntity getTE(ServerPlayer player, BlockPos pos)
    {
        Level world = player.level();

        if (world.isLoaded(pos))
        {
            return world.getBlockEntity(pos);
        }

        return null;
    }
}
