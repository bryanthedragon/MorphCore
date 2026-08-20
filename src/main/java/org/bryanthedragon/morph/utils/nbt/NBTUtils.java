package bryanthedragon.morph.utils.nbt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;

/**
 * NBT utils 
 */
public class NBTUtils
{
    public static void readFloatList(ListTag list, float[] array)
    {
        int count = Math.min(array.length, list.size());

        for (int i = 0; i < count; i++)
        {
            array[i] = list.getFloat(i);
        }
    }

    public static ListTag writeFloatList(ListTag list, float[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            list.add(FloatTag.valueOf(array[i]));
        }

        return list;
    }

    public static CompoundTag readInfiniteTag(ByteBuf buf)
    {
        int i = buf.readerIndex();
        byte b0 = buf.readByte();

        if (b0 == 0)
        {
            return null;
        }
        else
        {
            buf.readerIndex(i);

            try
            {
                return NbtIo.read(new ByteBufInputStream(buf), NbtAccounter.UNLIMITED);
            }
            catch (IOException ioexception)
            {
                throw new EncoderException(ioexception);
            }
        }
    }
}