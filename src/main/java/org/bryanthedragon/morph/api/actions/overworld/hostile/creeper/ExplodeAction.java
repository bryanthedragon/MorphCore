package bryanthedragon.morph.api.actions.overworld.hostile.creeper;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * Explode action
 * 
 * This action makes an explosion and also kills the player. Why kill also the 
 * player? Because it won't be so creeper if he won't die. 
 * 
 * EXPLOSIONS!!! Mr. Torgue approves this action. 
 */
public class ExplodeAction implements IAction
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

            if (entity instanceof Creeper creeper)
            {
                explosionPower = ObfuscationReflectionHelper.getPrivateValue(Creeper.class, creeper, "f_32272_");
                isPowered = creeper.isPowered();
            }
        }

        float f = isPowered ? 2.0F : 1.0F;
        target.level().explode(target, target.getX(), target.getY(), target.getZ(), explosionPower * f, Level.ExplosionInteraction.MOB);

        if (!(target instanceof Player player) || !player.isCreative())
        {
            target.hurt(target.damageSources().fellOutOfWorld(), target.getMaxHealth());
        }
    }
}
