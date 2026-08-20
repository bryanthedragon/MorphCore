package bryanthedragon.morph.utils;

import bryanthedragon.morph.MorphCore.MorphLib;

public class PayloadASM
{
    public static final int MIN_SIZE = 32767;

    /**
     * ASM hook which is used in {@link bryanthedragon.morph.core.transformers.CPacketCustomPayloadTransformer}
     */
    public static int getPayloadSize()
    {
        if (MorphLib.maxPacketSize == null)
        {
            return MIN_SIZE;
        }

        return Math.max(MIN_SIZE, MorphLib.maxPacketSize.get());
    }
}