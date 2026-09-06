package bryanthedragon.morph.network.morphlib.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import bryanthedragon.morph.utils.ByteBufUtils;
import bryanthedragon.morph.network.IMessage;

public class PacketDropItem implements IMessage
{
    public ItemStack stack = ItemStack.EMPTY;

    public PacketDropItem()
    {}

    public PacketDropItem(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        CompoundTag tagCompound = ByteBufUtils.readTag(buf);

        if (tagCompound != null)
        {
            this.stack = ItemStack.of(tagCompound);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if (!this.stack.isEmpty())
        {
            ByteBufUtils.writeTag(buf, this.stack.save(new CompoundTag()));
        }
    }
}