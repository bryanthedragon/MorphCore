package org.bryanthedragon.morph.api.abilities;

import net.minecraft.world.effect.MobEffects;

/**
 * Fire proof ability
 * 
 * This ability grants you fire immunity. So basically you're fire proof.
 */
public class FireProof extends PotionAbility
{
    public FireProof()
    {
        this.potion = MobEffects.FIRE_RESISTANCE;
    }
}