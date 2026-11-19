package org.bryanthedragon.morph.network;

import io.netty.buffer.ByteBuf;

/**
 * Network message
 *
 * Replacement for Forge's removed SimpleImpl {@code IMessage}. Implementations
 * must have a public no-arg constructor so they can be decoded.
 */
public interface IMessage
{
    public void fromBytes(ByteBuf buf);

    public void toBytes(ByteBuf buf);
}
