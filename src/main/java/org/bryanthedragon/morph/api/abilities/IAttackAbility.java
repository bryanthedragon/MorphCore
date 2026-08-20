package bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Ability attack interface
 * 
 * This interface is responsible for adding some additional effect on the 
 * target (used primarily for wither, and other stuff)
 */
public interface IAttackAbility
{
    /**
     * Do something with attacked entity
     */
    public void attack(Entity target, LivingEntity source);
}