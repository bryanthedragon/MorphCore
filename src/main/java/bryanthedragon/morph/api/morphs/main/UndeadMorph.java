package bryanthedragon.morph.api.morphs.main;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Undead morph 
 * 
 * This morph is responsible for preventing hurt noises caused by sun 
 * allergy.
 */
public class UndeadMorph extends EntityMorph
{
    protected static final ItemStack HELMET;

    static
    {
        CompoundTag unbreakable = new CompoundTag();

        unbreakable.setBoolean("Unbreakable", true);

        HELMET = new ItemStack(Items.LEATHER_HELMET);
        HELMET.setTagCompound(unbreakable);
    }

    @Override
    protected void updateEntity(LivingEntity target)
    {
        boolean preventNoise = !target.isBurning();

        if (preventNoise)
        {
            this.entity.setItemStackToSlot(EquipmentSlot.HEAD, HELMET);
        }

        super.updateEntity(target);

        if (preventNoise)
        {
            this.entity.setItemStackToSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        }
    }

    @Override
    public AbstractMorph create()
    {
        return new UndeadMorph();
    }
}