package bryanthedragon.morph.api.attacks;

import bryanthedragon.morph.api.abilities.IAttackAbility;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;


/**
 * Mob attack ability
 * 
 * This ability uses the mob's attack when the player attacks.
 * Both attacks will go through, taking the highest damage between
 * the player and the mob, and triggering any special effects the
 * mob does on attack.
 */
public class MobAttack implements IAttackAbility
{
    @Override
    public void attack(Entity target, LivingEntity source)
    {
        if (source instanceof Player)
        {
            Player player = (Player) source;
            IMorphing capability = Morphing.get(player);

            if(capability == null) return;

            AbstractMorph currentMorph = capability.getCurrentMorph();

            if(currentMorph == null) return;

            if(currentMorph instanceof EntityMorph)
            {
                EntityMorph currentEntityMorph = (EntityMorph) currentMorph;
                LivingEntity morphEntity = currentEntityMorph.getEntity(source.level());

                if (morphEntity instanceof Mob)
                {
                    ((Mob) morphEntity).doHurtTarget(target);
                }
            }
        }
    }
}
