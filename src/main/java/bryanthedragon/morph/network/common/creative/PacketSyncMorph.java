package bryanthedragon.morph.network.common.creative;

import net.minecraft.network.FriendlyByteBuf;
import bryanthedragon.morph.api.morphs.AbstractMorph;

public class PacketSyncMorph extends PacketMorph
{
    public int index;

    public PacketSyncMorph()
    {}

    public PacketSyncMorph(AbstractMorph morph, int index)
    {
        super(morph);
        this.index = index;
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        super.fromBytes(buf);

        this.index = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        super.toBytes(buf);

        buf.writeInt(this.index);
    }
}