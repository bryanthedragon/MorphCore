package org.bryanthedragon.morph.network;

import net.minecraftforge.network.NetworkEvent;

/**
 * Base of all network message handlers
 */
public interface IMessageHandler<REQ extends IMessage, REPLY extends IMessage>
{
    /**
     * Handle given message, optionally returning a message which is going
     * to be sent back to the sender
     */
    public REPLY onMessage(REQ message, NetworkEvent.Context context);
}
