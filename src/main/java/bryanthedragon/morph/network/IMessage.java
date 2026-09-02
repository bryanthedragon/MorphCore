package bryanthedragon.morph.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Network message
 *
 * Replacement for the old SimpleImpl IMessage interface. Implementations must
 * have a public no-arg constructor so they can be instantiated by the decoder.
 */
public interface IMessage
{
    public void fromBytes(FriendlyByteBuf buf);

    public void toBytes(FriendlyByteBuf buf);
}
