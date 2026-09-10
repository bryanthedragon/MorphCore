package bryanthedragon.morph.network.morphlib.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.network.ByteBufUtils;
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
            this.stack = new ItemStack(tagCompound);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if (!this.stack.isEmpty())
        {
            ByteBufUtils.writeTag(buf, this.stack.writeToNBT(new CompoundTag()));
        }
    }
}