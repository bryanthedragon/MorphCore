package org.bryanthedragon.morph.client.gui.framework.tooltips.styles;

import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.client.gui.utils.Area;
import org.bryanthedragon.morph.utils.color.ColorUtils;

public class LightTooltipStyle extends TooltipStyle
{
    @Override
    public void drawBackground(Area area)
    {
        GuiDraw.drawDropShadow(area.x, area.y, area.ex(), area.ey(), 4, ColorUtils.HALF_BLACK, 0);
        area.draw(0xffffffff);
    }

    @Override
    public int getTextColor()
    {
        return 0;
    }

    @Override
    public int getForegroundColor()
    {
        return 0;
    }
}