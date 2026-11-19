package org.bryanthedragon.morph.api.abilities;

import net.minecraft.world.entity.LivingEntity;

/**
 * Ability's base class
 * 
 * All it does, makes onMorph and onDemorph methods empty (so I didn't had a 
 * need to create those methods like in every ability).
 */
public abstract class Ability implements IAbility
{
    @Override
    public void onMorph(LivingEntity player)
    {

    }

    @Override
    public void onDemorph(LivingEntity player)
    {
        
    }
}