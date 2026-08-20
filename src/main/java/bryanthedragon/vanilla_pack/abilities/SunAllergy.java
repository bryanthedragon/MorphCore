package bryanthedragon.vanilla_pack.abilities;

import java.util.Random;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.inventory.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

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
    private MutableBlockPos pos = new MutableBlockPos(0, 0, 0);
    private Random random = new Random();

    @Override
    public void update(LivingEntity target)
    {
        if (!target.level().isDaytime() || target.level().isRemote)
        {
            return;
        }

        float brightness = target.getBrightness();
        boolean random = this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F;
        this.pos.setPos(target.posX, target.posY + target.getEyeHeight(), target.posZ);

        /* Taken from EntityZombie class and slightly modified */
        if (brightness > 0.5 && random && target.level().canSeeSky(pos))
        {
            boolean flag = true;
            ItemStack itemstack = target.getItemStackFromSlot(EquipmentSlot.HEAD);

            /* If target has a head slot on the head, then damage it */
            if (!itemstack.isEmpty())
            {
                boolean isCreativePlayer = target instanceof Player && ((Player) target).isCreative();

                /* Unless it's damagable or creative player wears it */
                if (itemstack.isItemStackDamageable() && !isCreativePlayer)
                {
                    itemstack.setItemDamage(itemstack.getItemDamage() + this.random.nextInt(2));

                    if (itemstack.getItemDamage() >= itemstack.getMaxDamage())
                    {
                        target.renderBrokenItemStack(itemstack);
                        target.setItemStackToSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag)
            {
                target.setFire(8);
            }
        }
    }
}