package bryanthedragon.morph.network.morphlib.common;

import bryanthedragon.morph.network.IMessage;

import io.netty.buffer.ByteBuf;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class PacketDropItem implements IMessage
{
    public ItemStack stack = ItemStack.EMPTY;

    public PacketDropItem()
    {
        
    }

    public PacketDropItem(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        CompoundTag tagCompound = new FriendlyByteBuf(buf).readNbt();

        if (tagCompound != null)
        {
            this.stack = ItemStack.of(tagCompound);
        }
        else
        {
            this.stack = ItemStack.EMPTY;
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        CompoundTag tag = this.stack.isEmpty() ? null : this.stack.save(new CompoundTag());
        new FriendlyByteBuf(buf).writeNbt(tag);
    }
}