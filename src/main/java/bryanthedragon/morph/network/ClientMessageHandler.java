package bryanthedragon.morph.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public abstract void run(final LocalPlayer player, final T message);

    @Override
    @OnlyIn(Dist.CLIENT)
    public IMessage handleClientMessage(final T message)
    {
        Minecraft.getInstance().execute(() -> ClientMessageHandler.this.run(Minecraft.getInstance().player, message));

        return null;
    }

    @Override
    public final IMessage handleServerMessage(final ServerPlayer player, final T message)
    {
        return null;
    }
}
