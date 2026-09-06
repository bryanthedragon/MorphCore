package bryanthedragon.morph.network.common.creative;

import io.netty.buffer.ByteBuf;
import bryanthedragon.morph.utils.NBTUtils;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.network.IMessage;

public class PacketMorph implements IMessage
{
    public AbstractMorph morph;

    public PacketMorph()
    {}

    public PacketMorph(AbstractMorph morph)
    {
        this.morph = morph;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.morph = MorphManager.INSTANCE.morphFromNBT(NBTUtils.readInfiniteTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        MorphUtils.morphToBuf(buf, this.morph);
    }
}