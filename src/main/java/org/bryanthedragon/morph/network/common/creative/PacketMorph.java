package org.bryanthedragon.morph.network.common.creative;

import io.netty.buffer.ByteBuf;

import org.bryanthedragon.morph.utils.nbt.NBTUtils;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.utils.morph.MorphUtils;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.network.IMessage;

public class PacketMorph implements IMessage
{
    public AbstractMorph morph;

    public PacketMorph()
    {
        
    }

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