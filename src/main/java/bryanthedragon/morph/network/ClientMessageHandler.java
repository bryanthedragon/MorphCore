package bryanthedragon.morph.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

/**
 * This class passes operation from Netty to Minecraft (Client) Thread. Also
 * prevents the server-side message handling method from appearing in client
 * message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class ClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>
{
    @OnlyIn(Dist.CLIENT)
    public abstract void run(final Player player, final T message);

    @OnlyIn(Dist.CLIENT)
    public IMessage handleClientMessage(final T message)
    {
        Minecraft.getInstance().execute(() -> { ClientMessageHandler.this.run(Minecraft.getInstance().player, message); } );
        return null;
    }

    public final IMessage handleServerMessage(final Player player, final T message)
    {
        return null;
    }
}