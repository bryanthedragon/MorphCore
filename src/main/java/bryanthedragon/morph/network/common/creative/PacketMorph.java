package bryanthedragon.morph.network.common.creative;

import bryanthedragon.morph.network.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import bryanthedragon.morph.utils.NBTUtils;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.AbstractMorph;

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
    public void fromBytes(FriendlyByteBuf buf)
    {
        this.morph = MorphManager.INSTANCE.morphFromNBT(NBTUtils.readInfiniteTag(buf));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        MorphUtils.morphToBuf(buf, this.morph);
    }
}