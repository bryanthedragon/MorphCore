package bryanthedragon.morph.api.actions;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.EntityMorph;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.Player;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
        if (target.level().isRemote)
        {
            return;
        }
        
        int explosionPower = 3;
        boolean isPowered = false;
        
        if (morph instanceof EntityMorph)
        {
            LivingEntity entity = ((EntityMorph) morph).getEntity();

            if (entity instanceof EntityCreeper)
            {
                explosionPower = ReflectionHelper.getPrivateValue(EntityCreeper.class, (EntityCreeper) entity, "explosionRadius", "field_82226_g");
                isPowered = ((EntityCreeper) entity).getPowered();
            }
        }

        float f = isPowered ? 2.0F : 1.0F;
        target.level().createExplosion(target, target.posX, target.posY, target.posZ, explosionPower * f, true);

        if (!(target instanceof Player) || (target instanceof Player && !((Player) target).isCreative()))
        {
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, target.getMaxHealth());
        }
    }
}