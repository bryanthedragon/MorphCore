package bryanthedragon.morph.network.common;

import bryanthedragon.morph.network.IMessage;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.network.FriendlyByteBuf;

public class PacketBlacklist implements IMessage
{
    public Set<String> blacklist = new TreeSet<String>();

    public PacketBlacklist()
    {}

    public PacketBlacklist(Set<String> blacklist)
    {
        this.blacklist.addAll(blacklist);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        for (int i = 0, c = buf.readInt(); i < c; i++)
        {
            this.blacklist.add(buf.readUtf());
        }
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(this.blacklist.size());

        Iterator<String> it = this.blacklist.iterator();

        while (it.hasNext())
        {
            buf.writeUtf(it.next());
        }
    }
}