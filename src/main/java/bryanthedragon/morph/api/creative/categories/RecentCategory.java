package bryanthedragon.morph.api.creative.categories;

import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.creative.sections.MorphSection;
import bryanthedragon.morph.api.morphs.AbstractMorph;

public class RecentCategory extends MorphCategory
{
    public RecentCategory(MorphSection parent, String title)
    {
        super(parent, title);
    }

    @Override
    protected void addMorph(AbstractMorph morph)
    {
        while (this.morphs.size() >= MorphCore.maxRecentMorphs.get())
        {
            this.morphs.remove(this.morphs.size() - 1);
        }

        this.morphs.add(0, morph);
    }

    @Override
    public boolean isEditable(AbstractMorph morph)
    {
        return this.morphs.indexOf(morph) != -1;
    }
}