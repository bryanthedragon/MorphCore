package bryanthedragon.morph.network.common;

import bryanthedragon.morph.network.IMessage;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import bryanthedragon.morph.api.MorphSettings;

/**
 * Packet that sents settings 
 */
public class PacketSettings implements IMessage
{
    public Map<String, MorphSettings> settings = new HashMap<String, MorphSettings>();

    public PacketSettings()
    {}

    public PacketSettings(Map<String, MorphSettings> settings)
    {
        this.settings.clear();
        this.settings.putAll(settings);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            String key = buf.readUtf();
            MorphSettings setting = new MorphSettings();

            setting.fromBytes(buf);
            this.settings.put(key, setting);
        }
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(this.settings.size());

        for (Map.Entry<String, MorphSettings> setting : this.settings.entrySet())
        {
            buf.writeUtf(setting.getKey());
            setting.getValue().toBytes(buf);
        }
    }
}