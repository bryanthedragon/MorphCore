package bryanthedragon.morph.client.gui.utils;


import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.framework.tooltips.styles.TooltipStyle;
import bryanthedragon.morph.utils.interpolation.IInterpolation;
import bryanthedragon.morph.utils.math.MathUtils;
import bryanthedragon.morph.utils.color.Color;
import bryanthedragon.morph.utils.color.ColorUtils;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class InterpolationRenderer
{
    public static void drawInterpolationPreview(IInterpolation interp, GuiContext context, int x, int y, float anchorX, float anchorY, int duration)
    {
        if (interp == null)
        {
            return;
        }

        final float iterations = 40;
        final float padding = 50;

        int w = 140;
        int h = 130;

        TooltipStyle style = TooltipStyle.get();
        String tooltip = interp.getTooltip();
        List<FormattedCharSequence> lines = tooltip.isEmpty() ? ImmutableList.of() : context.font.split(Component.literal(tooltip), w - 20);
        int lineHeight = context.font.lineHeight;
        int ah = lines.isEmpty() ? 0 : lines.size() * (lineHeight + 4);

        y = MathUtils.clamp(y, 0, context.screen.height - h - ah);

        x -= (int) (w * anchorX);
        y -= (int) (h * anchorY);

        Area.SHARED.set(x, y, w, h + ah);
        style.drawBackground(Area.SHARED);

        Color fg = ColorUtils.COLOR.set(style.getForegroundColor(), false);
        int fontColor = style.getTextColor();
        int foreground = fg.getRGBColor() & 0x00ffffff;
        int gridColor = 0x33000000 | foreground;
        int guideColor = 0x1c000000 | foreground;
        int curveColor = 0xff000000 | foreground;
        net.minecraft.client.gui.GuiGraphics graphics = context.graphics;

        graphics.drawString(context.font, interp.getName(), x + 10, y + 10, fontColor, false);

        for (int i = 0; i < lines.size(); i++)
        {
            graphics.drawString(context.font, lines.get(i), x + 10, y + h - 5 + i * (lineHeight + 4), fontColor, false);
        }

        graphics.fill(x + 9, y + 20, x + 11, y + h - 10, gridColor);
        graphics.fill(x + w / 2 - 1, y + 20, x + w / 2 + 1, y + h - 10, gridColor);
        graphics.fill(x + w - 11, y + 20, x + w - 9, y + h - 10, gridColor);
        graphics.fill(x + 10, y + 19, x + w - 10, y + 21, gridColor);
        graphics.fill(x + 10, y + 20 + (h - 30) / 2, x + w - 10, y + 22 + (h - 30) / 2, gridColor);
        graphics.fill(x + 10, y + h - 11, x + w - 10, y + h - 9, gridColor);
        graphics.fill(x + 10, y + h - 10 - (int) (padding / 2), x + w - 10, y + h - 9 - (int) (padding / 2), guideColor);
        graphics.fill(x + 10, y + 20 + (int) (padding / 2), x + w - 10, y + 21 + (int) (padding / 2), guideColor);

        float previousX = x + 10;
        float previousY = y + 20 + padding / 2 + (1 - interp.interpolate(0, 1, 0)) * (h - 30 - padding);
        for (int i = 1; i <= iterations; i++)
        {
            float factor1 = i / iterations;
            float value1 = 1 - interp.interpolate(0, 1, factor1);

            float currentX = x + 10 + factor1 * (w - 20);
            float currentY = y + 20 + padding / 2 + value1 * (h - 30 - padding);
            int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(currentX - previousX), Math.abs(currentY - previousY))));

            for (int step = 0; step <= steps; step++)
            {
                float part = step / (float) steps;
                int pointX = Math.round(previousX + (currentX - previousX) * part);
                int pointY = Math.round(previousY + (currentY - previousY) * part);
                graphics.fill(pointX - 1, pointY - 1, pointX + 2, pointY + 2, curveColor);
            }

            previousX = currentX;
            previousY = currentY;
        }

        graphics.drawString(context.font, "A", x + 14, (int) (y + h - 10 - padding / 2) + 4, fontColor, false);
        graphics.drawString(context.font, "B", x + w - 19, (int) (y + 20 + padding / 2) - lineHeight - 4, fontColor, false);

        float tick = ((context.tick + context.partialTicks) % (duration + 20)) / (float) duration;
        float factor = MathUtils.clamp(tick, 0, 1);
        int px = x + w - 5;
        int py = y + 20 + (int) (padding / 2) + (int) ((1 - interp.interpolate(0, 1, factor)) * (h - 30 - padding));

        graphics.fill(px - 2, py - 2, px + 2, py + 2, 0xff000000 | foreground);
    }
}