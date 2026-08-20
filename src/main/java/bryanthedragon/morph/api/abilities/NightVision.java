package bryanthedragon.morph.api.abilities;

import net.minecraft.world.effect.MobEffects;

/**
 * Night vision ability
 * 
 * Grants night vision effect for given morph. Mostly used by bat.
 */
public class NightVision extends PotionAbility
{
    public NightVision()
    {
        this.potion = MobEffects.NIGHT_VISION;
    }
}