package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

/**
 * Potions action
 * 
 * This action is responsible for throwing a splash potion in the direction 
 * where player looks.
 * 
 * This action may throw instant harm, slowness, weakness or poison splash 
 * potions depending on randomness, but most of the time it'll be instant harm 
 * potion.
 * 
 * Of course, some of the code of this action was taken fron 
 * {@link Witch}.
 */
public class Potions implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        Level world = target.level();

        if (world.isClientSide())
        {
            return;
        }

        if (target instanceof Player player && player.getAttackStrengthScale(0.0F) < 1.0F)
        {
            return;
        }

        Vec3 look = target.getLookAngle();
        Potion effect = net.minecraft.world.item.alchemy.Potions.HARMING;

        if (target.getRandom().nextFloat() < 0.2)
        {
            effect = net.minecraft.world.item.alchemy.Potions.SLOWNESS;
        }
        else if (target.getRandom().nextFloat() < 0.1)
        {
            effect = net.minecraft.world.item.alchemy.Potions.WEAKNESS;
        }
        else if (target.getRandom().nextFloat() < 0.05)
        {
            effect = net.minecraft.world.item.alchemy.Potions.POISON;
        }

        ItemStack stack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), effect);
        ThrownPotion potion = new ThrownPotion(world, target);

        potion.setItem(stack);
        potion.setXRot(potion.getXRot() + 20.0F);
        potion.shoot(look.x, look.y, look.z, 0.85F, 2.0F);

        world.addFreshEntity(potion);

        if (target instanceof Player player)
        {
            player.resetAttackStrengthTicker();
        }
    }
}
