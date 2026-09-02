package bryanthedragon.morph.network.morphlib.common;

import net.minecraft.network.FriendlyByteBuf;

public class PacketBoolean extends PacketAnswer<Boolean>
{
    public PacketBoolean()
    { }

    public PacketBoolean(int callbackID, boolean value)
    {
        super(callbackID, value);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        this.callBackID = buf.readInt();
        this.answer = buf.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(this.callBackID);
        buf.writeBoolean(this.getValue());
    }
}
