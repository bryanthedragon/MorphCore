package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Explode action
 * 
 * This action makes an explosion and also kills the player. Why kill also the 
 * player? Because it won't be so creeper if he won't die. 
 * 
 * EXPLOSIONS!!! Mr. Torgue approves this action. 
 */
public class Explode implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        if (target.level().isClientSide)
        {
            return;
        }
        
        int explosionPower = 3;
        boolean isPowered = false;
        
        if (morph instanceof EntityMorph)
        {
            LivingEntity entity = ((EntityMorph) morph).getEntity();

            if (entity instanceof Creeper)
            {
                explosionPower = ((Creeper) entity).getExplosionRadius();
                isPowered = ((Creeper) entity).isPowered();
            }
        }

        float f = isPowered ? 2.0F : 1.0F;
        target.level().explode(target, target.getX(), target.getY(), target.getZ(), explosionPower * f, true, null);

        if (!(target instanceof Player) || (target instanceof Player && !((Player) target).isCreative()))
        {
            target.hurt(DamageSource.OUT_OF_WORLD, target.getMaxHealth());
        }
    }
}