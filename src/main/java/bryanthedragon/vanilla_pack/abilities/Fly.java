package bryanthedragon.vanilla_pack.abilities;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;

/**
 * Fly ability
 * 
 * Allows player to fly as in creative. Mostly used by flying (captain) morphs. 
 */
public class Fly extends Ability
{
    @Override
    public void onMorph(LivingEntity target)
    {
        if (target instanceof Player)
        {
            Player player = (Player) target;

            if (!player.capabilities.allowFlying)
            {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
            }
        }
    }

    @Override
    public void update(LivingEntity target)
    {
        this.onMorph(target);
    }

    @Override
    public void onDemorph(LivingEntity target)
    {
        if (target instanceof Player)
        {
            Player player = (Player) target;

            if (!player.isCreative())
            {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                player.sendPlayerAbilities();
            }
        }
    }
}