package bryanthedragon.vanilla_pack.attacks;

import bryanthedragon.morph.api.abilities.IAttackAbility;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.LivingEntity;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.EntityMorph;
import net.minecraft.entity.player.Player;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;

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
            if(capability == null)
                return;
            AbstractMorph currentMorph = capability.getCurrentMorph();
            if(currentMorph == null)
                return;
            if(currentMorph instanceof EntityMorph)
            {
                EntityMorph currentEntityMorph = (EntityMorph) currentMorph;
                currentEntityMorph.getEntity(source.world).attackEntityAsMob(target);
            }
        }
    }
}
