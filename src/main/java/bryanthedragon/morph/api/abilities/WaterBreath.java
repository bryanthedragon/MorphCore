package bryanthedragon.vanilla_pack.abilities;

import bryanthedragon.morph.api.abilities.Ability;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
                    target.setAir(300);
                }
                else
                {
                    int air = morphing.getSquidAir() - 1;

                    if (air <= -20)
                    {
                        air = 0;
                        target.attackEntityFrom(DamageSource.DROWN, 2.0F);
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
            morphing.setSquidAir(target.getAir());
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
            target.setAir(morphing.getSquidAir());
            morphing.setHasSquidAir(false);
        }
    }
}