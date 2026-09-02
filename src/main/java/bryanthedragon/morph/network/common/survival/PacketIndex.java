package bryanthedragon.morph.network.common.survival;

import bryanthedragon.morph.network.IMessage;

import net.minecraft.network.FriendlyByteBuf;

public abstract class PacketIndex implements IMessage
{
    public int index;

    public PacketIndex()
    {}

    public PacketIndex(int index)
    {
        this.index = index;
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        this.index = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(index);
    }
}