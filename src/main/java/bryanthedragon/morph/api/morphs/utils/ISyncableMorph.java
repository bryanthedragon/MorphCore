package bryanthedragon.morph.api.morphs.utils;

import bryanthedragon.morph.api.models.IMorphProvider;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.bodypart.BodyPart;
import bryanthedragon.morph.bodypart.IBodyPartProvider;

public interface ISyncableMorph
{
    public void pause(AbstractMorph previous, int offset);

    public boolean isPaused();

    default public void resume()
    {
        Object morph = this;

        if (morph instanceof IMorphProvider)
        {
            morph = ((IMorphProvider) morph).getMorph();
        }
        
        if (morph instanceof IAnimationProvider)
        {
            ((IAnimationProvider) morph).getAnimation().paused = false;
        }
        
        if (morph instanceof IBodyPartProvider)
        {
            for (BodyPart part : ((IBodyPartProvider) morph).getBodyPart().parts)
            {
                if (!part.morph.isEmpty() && part.morph.get() instanceof ISyncableMorph)
                {
                    ((ISyncableMorph) part.morph.get()).resume();
                }
            }
        }
    }
}