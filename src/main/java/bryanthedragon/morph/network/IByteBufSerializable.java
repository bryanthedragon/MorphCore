package bryanthedragon.morph.network;

import net.minecraft.network.FriendlyByteBuf;

public interface IByteBufSerializable
{
    public void fromBytes(FriendlyByteBuf buffer);

    public void toBytes(FriendlyByteBuf buffer);
}
