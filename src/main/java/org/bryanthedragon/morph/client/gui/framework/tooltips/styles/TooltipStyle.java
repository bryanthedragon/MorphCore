package bryanthedragon.morph.client.gui.framework.tooltips.styles;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.utils.Area;

public abstract class TooltipStyle
{
    public static final TooltipStyle LIGHT = new LightTooltipStyle();
    public static final TooltipStyle DARK = new DarkTooltipStyle();

    public static TooltipStyle get()
    {
        return get(MorphLib.tooltipStyle.get());
    }

    public static TooltipStyle get(int style)
    {
        if (style == 0)
        {
            return LIGHT;
        }

        return DARK;
    }

    public abstract void drawBackground(Area area);

    public abstract int getTextColor();

    public abstract int getForegroundColor();
}