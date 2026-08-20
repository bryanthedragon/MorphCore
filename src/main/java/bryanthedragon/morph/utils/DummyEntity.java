package bryanthedragon.morph.utils;

import java.util.Arrays;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.init.Items;
import net.minecraft.inventory.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

/**
 * Dummy entity
 *
 * This class is used in model editor as a player substitution for the model
 * methods.
 */
public class DummyEntity extends LivingEntity
{
    private final ItemStack[] held;
    public ItemStack right;
    public ItemStack left;

    public DummyEntity(Level worldIn)
    {
        super(worldIn);

        this.right = new ItemStack(Items.DIAMOND_SWORD);
        this.left = new ItemStack(Items.GOLDEN_SWORD);
        this.held = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    }

    public void setItems(ItemStack left, ItemStack right)
    {
        this.left = left;
        this.right = right;
    }

    public void toggleItems(boolean toggle)
    {
        int main = EquipmentSlot.MAINHAND.getSlotIndex();
        int off = EquipmentSlot.OFFHAND.getSlotIndex();

        if (toggle)
        {
            this.held[main] = this.right;
            this.held[off] = this.left;
        }
        else
        {
            this.held[main] = this.held[off] = ItemStack.EMPTY;
        }
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return Arrays.asList(this.held);
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlot slotIn)
    {
        return this.held[slotIn.getSlotIndex()];
    }

    @Override
    public void setItemStackToSlot(EquipmentSlot slotIn, ItemStack stack)
    {
        this.held[slotIn.getSlotIndex()] = stack;
    }

    @Override
    public EnumHandSide getPrimaryHand()
    {
        return EnumHandSide.RIGHT;
    }
}