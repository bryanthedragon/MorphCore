package bryanthedragon.morph.api.abilities;


import net.minecraft.world.entity.LivingEntity;

/**
 * Prevent fall damage ability
 * 
 * This ability is responsible for prevent the fall damage, and it is doing it 
 * by modifying player's "fallDistance" field and setting it (or rather reseting) 
 * to 0.0F.
 */
public class PreventFall extends Ability
{
    @Override
    public void update(LivingEntity player)
    {
        player.fallDistance = 0.0F;
    }
}