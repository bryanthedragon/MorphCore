package bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;

/**
 * Climbing ability
 * 
 * This ability makes player climb on the wall. Don't add this ability to the 
 * any other mobs except Spiders, otherwise player will turn into Spider man.
 */
public class Climb extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        /* Works properly only on client side :( */
        if (target.horizontalCollision)
        {
            target.setDeltaMovement(target.getDeltaMovement().x, target.isShiftKeyDown() ? 0 : 0.2D, target.getDeltaMovement().z);
        }

        /* Fucking server doesn't handles climbing properly, that's why I 
         * have to reset fall distance in order to save player lives */
        target.fallDistance = 0.0F;
    }
}