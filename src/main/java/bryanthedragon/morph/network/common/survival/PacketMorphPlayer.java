package bryanthedragon.morph.network.common.survival;

import bryanthedragon.morph.network.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.AbstractMorph;

public class PacketMorphPlayer implements IMessage
{
    public int id;
    public AbstractMorph morph;

    public PacketMorphPlayer()
    {}

    public PacketMorphPlayer(int id, AbstractMorph morph)
    {
        this.id = id;
        this.morph = morph;
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        this.id = buf.readInt();
        this.morph = MorphUtils.morphFromBuf(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(this.id);
        MorphUtils.morphToBuf(buf, this.morph);
    }
}