package bryanthedragon.morph.client.gui.survival;

import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.creative.MorphList;
import bryanthedragon.morph.api.creative.categories.AcquiredCategory;
import bryanthedragon.morph.api.creative.sections.MorphSection;
import bryanthedragon.morph.api.creative.sections.UserSection;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.client.gui.GuiMorphs;
import bryanthedragon.morph.client.gui.creative.GuiMorphSection;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.function.Consumer;

public class GuiSurvivalMorphs extends GuiMorphs
{
    public AcquiredCategory acquired;

    public GuiSurvivalMorphs(Minecraft mc)
    {
        super(mc);
    }

    public void setupSections(boolean creative, Consumer<GuiMorphSection> callback)
    {
        Minecraft mc = Minecraft.getInstance();
        MorphList list = MorphManager.INSTANCE.list;
        IMorphing cap = Morphing.get(mc.player);

        MorphSection section;
        AcquiredCategory category;

        if (creative || MorphCore.allowMorphingIntoCategoryMorphs.get())
        {
            UserSection user = (UserSection) list.sections.get(0);

            section = user;
            section.update(mc.world);
            category = user.acquired;
        }
        else
        {
            section = new MorphSection("user");
            category = new AcquiredCategory(section, "acquired");

            category.setMorphs(cap == null ? Collections.emptyList() : cap.getAcquiredMorphs());
            section.add(category);
        }

        GuiMorphSection element = section.getGUI(mc, null, callback);

        element.flex();

        this.removeAll();
        this.add(element);
        this.selected = element;
        this.acquired = category;

        this.sections.clear();
        this.sections.add(this.selected);
    }

    @Override
    public void setSelected(AbstractMorph morph)
    {
        super.setSelected(morph);

        if (morph != null)
        {
            AbstractMorph found = this.acquired.getEqual(morph);

            if (found != null)
            {
                this.selected.category = this.acquired;
                this.selected.morph = found;
            }
        }
    }

    public boolean isAcquiredSelected()
    {
        return this.selected != null && this.selected.category == this.acquired;
    }
}