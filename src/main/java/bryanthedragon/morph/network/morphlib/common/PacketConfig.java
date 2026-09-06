package bryanthedragon.morph.network.morphlib.common;

import io.netty.buffer.ByteBuf;
import bryanthedragon.morph.config.Config;
import bryanthedragon.morph.utils.ByteBufUtils;
import bryanthedragon.morph.network.IMessage;

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
    public void fromBytes(ByteBuf buf)
    {
        this.config = new Config(ByteBufUtils.readUTF8String(buf));
        this.config.fromBytes(buf);
        this.overwrite = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.config.id);

        this.config.toBytes(buf);
        buf.writeBoolean(this.overwrite);
    }
}