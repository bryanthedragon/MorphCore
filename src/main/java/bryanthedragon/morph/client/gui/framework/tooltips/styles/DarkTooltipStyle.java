package bryanthedragon.morph.client.gui.framework.tooltips.styles;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.client.gui.utils.Area;

public class DarkTooltipStyle extends TooltipStyle
{
    @Override
    public void drawBackground(Area area)
    {
        int color = MorphLib.primaryColor.get();

        GuiDraw.drawDropShadow(area.x, area.y, area.ex(), area.ey(), 6, 0x44000000 + color, color);
        area.draw(0xff000000);
    }

    @Override
    public int getTextColor()
    {
        return 0xffffff;
    }

    @Override
    public int getForegroundColor()
    {
        return MorphLib.primaryColor.get();
    }
}