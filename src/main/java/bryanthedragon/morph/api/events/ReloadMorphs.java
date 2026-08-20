package bryanthedragon.morph.api.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

/**
 * Reload morphs event
 * 
 * This event gets fired when creative morphs picker gets initiated
 */
@OnlyIn(Dist.CLIENT)
public class ReloadMorphs extends Event
{
    public ReloadMorphs()
    {
        
    }
}