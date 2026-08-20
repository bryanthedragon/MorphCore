package bryanthedragon.morph.api.abilities;

import java.util.Random;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Sun allergy ability 
 * 
 * This abilitiy does cool stuff. It sets player on fire when he's on the sun. 
 * It will be used by the coolest mobs in the game skeleton and zombie.
 * 
 * This is more like a disability than an ability *Ba-dum-pam-dum-tsss*
 */
public class SunAllergy extends Ability
{
    private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
    private Random random = new Random();

    @Override
    public void update(LivingEntity target)
    {
        if (!target.level().isDay() || target.level().isClientSide())
        {
            return;
        }

        float brightness = target.getBrightness();
        boolean random = this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F;
        this.pos.set(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());

        /* Taken from EntityZombie class and slightly modified */
        if (brightness > 0.5 && random && target.level().canSeeSky(pos))
        {
            boolean flag = true;
            ItemStack itemstack = target.getItemBySlot(EquipmentSlot.HEAD);

            /* If target has a head slot on the head, then damage it */
            if (!itemstack.isEmpty())
            {
                boolean isCreativePlayer = target instanceof Player && ((Player) target).isCreative();

                /* Unless it's damagable or creative player wears it */
                if (itemstack.isDamageableItem() && !isCreativePlayer)
                {
                    itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));

                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage())
                    {
                        target.renderBrokenItemStack(itemstack);
                        target.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag)
            {
                target.setSecondsOnFire(8);
            }
        }
    }
}