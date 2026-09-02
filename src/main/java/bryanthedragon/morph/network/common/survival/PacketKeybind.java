package bryanthedragon.morph.network.common.survival;

import net.minecraft.network.FriendlyByteBuf;

public class PacketKeybind extends PacketIndex
{
    public int keybind;

    public PacketKeybind()
    {
        super();
    }

    public PacketKeybind(int index, int keybind)
    {
        super(index);

        this.keybind = keybind;
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        super.fromBytes(buf);

        this.keybind = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeInt(this.keybind);
    }
}