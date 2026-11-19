package org.bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Abstract potion ability
 * 
 * This is class is responsible for adding specific given potion effect from 
 * subclasses. You can also give the duration for the potion effect. 
 */
public abstract class PotionAbility extends Ability
{
    protected MobEffect potion;
    protected int duration = 1200;

    @Override
    public void update(LivingEntity target)
    {
        MobEffectInstance effect = target.getEffect(this.potion);

        if (effect == null || effect.getDuration() < 300)
        {
            this.onDemorph(target);
            this.onMorph(target);
        }
    }

    @Override
    public void onMorph(LivingEntity target)
    {
        target.addEffect(new MobEffectInstance(this.potion, this.duration, 0, false, false));
    }

    @Override
    public void onDemorph(LivingEntity target)
    {
        target.removeEffect(this.potion);
    }
}