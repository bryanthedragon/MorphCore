package org.bryanthedragon.morph.network.common;

import org.bryanthedragon.morph.api.settings.MorphSettings;
import org.bryanthedragon.morph.network.IMessage;
import org.bryanthedragon.morph.utils.bytebuf.ByteBufUtils;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * Packet that sends settings 
 */
public class PacketSettings implements IMessage
{
    public Map<String, MorphSettings> settings = new HashMap<String, MorphSettings>();

    public PacketSettings()
    {

    }

    public PacketSettings(Map<String, MorphSettings> settings)
    {
        this.settings.clear();
        this.settings.putAll(settings);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            String key = ByteBufUtils.readUTF8String(buf);
            MorphSettings setting = new MorphSettings();

            setting.fromBytes(buf);
            this.settings.put(key, setting);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.settings.size());

        for (Map.Entry<String, MorphSettings> setting : this.settings.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, setting.getKey());
            setting.getValue().toBytes(buf);
        }
    }
}