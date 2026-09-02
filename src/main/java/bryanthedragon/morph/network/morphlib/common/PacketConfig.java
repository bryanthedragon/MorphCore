package bryanthedragon.morph.network.morphlib.common;

import bryanthedragon.morph.network.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import bryanthedragon.morph.config.Config;

public class PacketConfig implements IMessage
{
    public Config config;
    public boolean overwrite;

    public PacketConfig()
    {}

    public PacketConfig(Config config)
    {
        this(config, false);
    }

    public PacketConfig(Config config, boolean overwrite)
    {
        this.config = config;
        this.overwrite = overwrite;
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        this.config = new Config(buf.readUtf());
        this.config.fromBytes(buf);
        this.overwrite = buf.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeUtf(this.config.id);

        this.config.toBytes(buf);
        buf.writeBoolean(this.overwrite);
    }
}