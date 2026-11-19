package org.bryanthedragon.morph.network.morphlib.common;

import io.netty.buffer.ByteBuf;
import org.bryanthedragon.morph.utils.bytebuf.ByteBufUtils;
import org.bryanthedragon.morph.network.IMessage;

import java.io.Serializable;

public class PacketAnswer<T extends Serializable> implements IMessage
{
    protected int callBackID;
    protected T answer;

    public PacketAnswer()
    { 
        
    }

    public PacketAnswer(int callBackID, T answer)
    {
        this.callBackID = callBackID;
        this.answer = answer;
    }

    public int getCallbackID()
    {
        return this.callBackID;
    }

    public T getValue()
    {
        return this.answer;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void fromBytes(ByteBuf buf)
    {
        this.callBackID = buf.readInt();

        try
        {
            this.answer = (T) ByteBufUtils.readObject(buf);
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callBackID);
        ByteBufUtils.writeObject(buf, this.answer);
    }
}
