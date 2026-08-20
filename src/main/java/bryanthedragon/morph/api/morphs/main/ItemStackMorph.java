package bryanthedragon.morph.api.morphs.main;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackMorph extends AbstractMorph
{
    public boolean lighting = true;

    public abstract void setStack(ItemStack stack);

    public abstract ItemStack getStack();

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ItemStackMorph)
        {
            ItemStackMorph morph = (ItemStackMorph) obj;

            result = result && this.lighting == morph.lighting;
        }

        return result;
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof ItemStackMorph)
        {
            ItemStackMorph morph = (ItemStackMorph) from;

            this.lighting = morph.lighting;
        }
    }

    @Override
    public void toNBT(CompoundTag tag)
    {
        super.toNBT(tag);

        if (!this.lighting)
        {
            tag.putBoolean("Lighting", this.lighting);
        }
    }

    @Override
    public void fromNBT(CompoundTag tag)
    {
        super.fromNBT(tag);

        if (tag.contains("Lighting"))
        {
            this.lighting = tag.getBoolean("Lighting");
        }
    }
}