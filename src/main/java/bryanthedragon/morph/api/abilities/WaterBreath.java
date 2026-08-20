package bryanthedragon.morph.api.abilities;

import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Water breath ability
 * 
 * This ability grants its owner ability to stay in water and refill its air. 
 */
public class WaterBreath extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        updateAir(target);
    }

    private void updateAir(LivingEntity target)
    {
        if (target instanceof Player)
        {
            IMorphing morphing = Morphing.get((Player) target);

            if (morphing != null)
            {
                if (target.isInWater())
                {
                    morphing.setSquidAir(300);
                    target.setAirSupply(300);
                }
                else
                {
                    int air = morphing.getSquidAir() - 1;

                    if (air <= -20)
                    {
                        air = 0;
                        target.hurt(target.damageSources().drown(), 2.0F);
                    }

                    morphing.setSquidAir(air);
                }
            }
        }
    }

    /**
     * On morph, show squid air
     */
    @Override
    public void onMorph(LivingEntity target)
    {
        IMorphing morphing = Morphing.get((Player) target);

        if (morphing != null)
        {
            morphing.setSquidAir(target.getAirSupply());
            morphing.setHasSquidAir(true);
        }
    }

    /**
     * On demorph, show regular player air again
     */
    @Override
    public void onDemorph(LivingEntity target)
    {
        IMorphing morphing = Morphing.get((Player) target);

        if (morphing != null)
        {
            target.setAirSupply(morphing.getSquidAir());
            morphing.setHasSquidAir(false);
        }
    }
}