package bryanthedragon.morph.network.morphlib.common;

import bryanthedragon.morph.network.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

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
    public void fromBytes(FriendlyByteBuf buf)
    {
        CompoundTag tagCompound = buf.readNbt();

        if (tagCompound != null)
        {
            this.stack = ItemStack.of(tagCompound);
        }
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        if (!this.stack.isEmpty())
        {
            buf.writeNbt(this.stack.save(new CompoundTag()));
        }
    }
}