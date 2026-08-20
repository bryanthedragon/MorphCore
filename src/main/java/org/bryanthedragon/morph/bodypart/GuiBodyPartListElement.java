package bryanthedragon.morph.bodypart;

import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.list.GuiListElement;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

/**
 * Body part list which displays body parts
 */
public class GuiBodyPartListElement extends GuiListElement<BodyPart>
{
    public GuiBodyPartListElement(Minecraft mc, Consumer<List<BodyPart>> callback)
    {
        super(mc, callback);

        this.scroll.scrollItemSize = 24;
    }

    @Override
    protected void drawElementPart(BodyPart element, int i, int x, int y, boolean hover, boolean selected)
    {
        GuiContext context = GuiBase.getCurrent();

        if (!element.morph.isEmpty())
        {
            GuiDraw.scissor(x, y, this.scroll.w, this.scroll.scrollItemSize, context);
            element.morph.get().renderOnScreen(this.mc.player, x + this.scroll.w - 16, y + 30, 20, 1);
            GuiDraw.unscissor(context);
        }

        super.drawElementPart(element, i, x, y, hover, selected);
    }

    @Override
    protected String elementToString(BodyPart element)
    {
        String label = element.limb.isEmpty() ? "_" : element.limb;

        if (!element.morph.isEmpty())
        {
            AbstractMorph morph = element.morph.get();

            if (morph.hasCustomName())
            {
                label += " - " + element.morph.get().getDisplayName();
            }
        }

        return label;
    }
}