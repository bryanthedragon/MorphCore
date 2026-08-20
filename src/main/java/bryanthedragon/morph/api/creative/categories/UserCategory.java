package bryanthedragon.morph.api.creative.categories;

import bryanthedragon.morph.api.creative.sections.MorphSection;
import bryanthedragon.morph.api.creative.sections.UserSection;
import bryanthedragon.morph.api.morphs.AbstractMorph;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class UserCategory extends MorphCategory
{
    public UserCategory(MorphSection parent, String title)
    {
        super(parent, title);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getTitle()
    {
        return this.title;
    }

    @Override
    public boolean isHidden()
    {
        return this.hidden;
    }

    @Override
    public void addMorph(AbstractMorph morph)
    {
        super.addMorph(morph);

        if (this.parent instanceof UserSection)
        {
            ((UserSection) this.parent).save();
        }
    }

    @Override
    public boolean isEditable(AbstractMorph morph)
    {
        return this.morphs.indexOf(morph) != -1;
    }

    @Override
    public void edit(AbstractMorph morph)
    {
        int index = this.morphs.indexOf(morph);

        if (index >= 0 && this.parent instanceof UserSection)
        {
            ((UserSection) this.parent).save();
        }
    }

    @Override
    public boolean remove(AbstractMorph morph)
    {
        boolean result = super.remove(morph);

        if (result && this.parent instanceof UserSection)
        {
            ((UserSection) this.parent).save();
        }

        return result;
    }
}