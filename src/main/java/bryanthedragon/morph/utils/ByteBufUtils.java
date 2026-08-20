package bryanthedragon.morph.utils;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class ByteBufUtils
{
    public static void writeUTF8String(ByteBuf to, String value)
    {
        writeByteArray(to, value.getBytes(StandardCharsets.UTF_8));
    }

    public static String readUTF8String(ByteBuf from)
    {
        return new String(readByteArray(from), StandardCharsets.UTF_8);
    }

    public static void writeObject(ByteBuf to, Object object)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try
        {
            ObjectOutputStream output = new ObjectOutputStream(bos);

            output.writeObject(object);
            output.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        writeByteArray(to, bos.toByteArray());
    }

    @Nullable
    public static Object readObject(ByteBuf from)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(readByteArray(from));
        Object result = null;

        try
        {
            ObjectInputStream input = new ObjectInputStream(bis);

            result = input.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] readByteArray(ByteBuf from)
    {
        int size = from.readInt();
        ByteBuf bytes = from.readBytes(size);

        byte[] array = new byte[bytes.capacity()];

        bytes.getBytes(0, array);

        return array;
    }

    public static void writeByteArray(ByteBuf to, byte[] array)
    {
        to.writeInt(array.length);
        to.writeBytes(array);
    }
}
