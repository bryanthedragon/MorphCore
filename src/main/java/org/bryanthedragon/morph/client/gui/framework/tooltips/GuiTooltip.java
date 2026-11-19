package org.bryanthedragon.morph.client.gui.framework.tooltips;

import org.bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.utils.Area;

public class GuiTooltip
{
    public GuiElement element;
    public Area area = new Area();

    public void set(GuiContext context, GuiElement element)
    {
        this.element = element;

        if (element != null)
        {
            this.area.copy(element.area);
            this.area.x = context.globalX(this.area.x);
            this.area.y = context.globalY(this.area.y);
        }
    }

    public void draw(ITooltip tooltip, GuiContext context)
    {
        if (this.element == null || tooltip == null)
        {
            return;
        }

        tooltip.drawTooltip(context);
    }

    public void drawTooltip(GuiContext context)
    {
        if (this.element != null)
        {
            this.element.drawTooltip(context, this.area);
        }
    }
}