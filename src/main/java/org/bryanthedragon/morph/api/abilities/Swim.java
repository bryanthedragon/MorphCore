package bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;

/**
 * Swim ability
 * 
 * This ability makes player a better swimmer. What it does, it basically 
 * increases the swim speed and also gives player more control over vertical 
 * movement. 
 */
public class Swim extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        updateMotion(target);
    }
    
    private void updateMotion(LivingEntity target)
    {
        double speed = 0.35;

        if (target.isInWater())
        {
            if (target.getDeltaMovement().x != 0 || target.getDeltaMovement().z != 0)
            {
                float a = target.getYRot() / 180 * (float) Math.PI;

                float f1 = (float) Math.sin(a);
                float f2 = (float) Math.cos(a);

                target.setDeltaMovement(target.getDeltaMovement().add((target.getDeltaMovement().x * f2 - target.getDeltaMovement().z * f1) * speed, 0, (target.getDeltaMovement().z * f2 + target.getDeltaMovement().x * f1) * speed));
                target.setDeltaMovement(target.getDeltaMovement().add(0, -Mth.sin(target.getXRot() / 180 * (float) Math.PI) * target.getDeltaMovement().z * speed, 0));
            }
            else
            {
                target.setDeltaMovement(target.getDeltaMovement().multiply(0.6, 0.6, 0.6));
                if (target.getDeltaMovement().y < 0)
                {
                    target.setDeltaMovement(target.getDeltaMovement().x, 0, target.getDeltaMovement().z);
                }
            }
        }
    }
}