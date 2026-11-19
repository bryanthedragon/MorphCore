package org.bryanthedragon.morph.client.gui.framework.elements.buttons;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.text.ITextColoring;
import org.bryanthedragon.morph.client.gui.utils.Icons;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.config.values.ValueBoolean;
import org.bryanthedragon.morph.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

import java.util.function.Consumer;

public class GuiToggleElement extends GuiClickElement<GuiToggleElement> implements ITextColoring
{
    public IKey label;
    public int color = 0xffffff;
    public boolean textShadow = true;
    private boolean state;

    public GuiToggleElement(Minecraft mc, ValueBoolean value)
    {
        this(mc, value, null);
    }

    public GuiToggleElement(Minecraft mc, ValueBoolean value, @Nullable Consumer<GuiToggleElement> callback)
    {
        this(mc, IKey.lang(value.getLabelKey()), value.get(), callback == null ? (toggle) -> value.set(toggle.isToggled()) : (toggle) ->
        {
            value.set(toggle.isToggled());
            callback.accept(toggle);
        });
        this.tooltip(IKey.lang(value.getCommentKey()));
    }

    public GuiToggleElement(Minecraft mc, IKey label, @Nullable Consumer<GuiToggleElement> callback)
    {
        this(mc, label, false, callback);
    }

    public GuiToggleElement(Minecraft mc, IKey label, boolean state, @Nullable Consumer<GuiToggleElement> callback)
    {
        super(mc, callback);

        this.label = label;
        this.state = state;
        this.flex().h(14);
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.color(color, shadow);
    }

    public GuiToggleElement label(IKey label)
    {
        this.label = label;

        return this;
    }

    public GuiToggleElement toggled(boolean state)
    {
        this.state = state;

        return this;
    }

    public GuiToggleElement color(int color)
    {
        return this.color(color, true);
    }

    public GuiToggleElement color(int color, boolean textShadow)
    {
        this.color = color;
        this.textShadow = textShadow;

        return this;
    }

    public boolean isToggled()
    {
        return this.state;
    }

    @Override
    protected void click(int mouseWheel)
    {
        this.state = !this.state;

        super.click(mouseWheel);
    }

    @Override
    protected GuiToggleElement get()
    {
        return this;
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        if (MorphLib.enableCheckboxRendering.get())
        {
            int y = this.area.my(this.font.lineHeight - 1);

            context.graphics.fill(this.area.x, y - 3, this.area.x + 11, y + 8, 0xff000000 + MorphLib.primaryColor.get());

            if (MorphLib.enableBorders.get())
            {
                GuiDraw.drawOutline(this.area.x, y - 3, this.area.x + 11, y + 8, 0xff000000);
            }

            if (this.state)
            {
                context.graphics.drawString(this.font, "x", this.area.x + 3, y - 2, 0xffffff, this.textShadow);
            }

            context.graphics.drawString(this.font, this.label.get(), this.area.x + 14, y, this.color, this.textShadow);

            if (!this.isEnabled())
            {
                context.graphics.fill(this.area.x, y - 3, this.area.x + 11, y + 8, ColorUtils.HALF_BLACK);
                GuiDraw.drawOutlinedIcon(Icons.LOCKED, this.area.x + 5, y + 2, 0xffffffff, 0.5F, 0.5F);
            }
        }
        else
        {
            context.graphics.drawString(this.font, this.label.get(), this.area.x, this.area.my(this.font.lineHeight - 1), this.color, this.textShadow);

            /* Draw toggle switch */
            int w = 16;
            int h = 10;
            int x = this.area.ex() - w - 2;
            int y = this.area.my();
            int color = MorphLib.primaryColor.get();

            if (this.hover)
            {
                color = ColorUtils.multiplyColor(color, 0.85F);
            }

            /* Draw toggle background */
            context.graphics.fill(x, y - h / 2, x + w, y - h / 2 + h, 0xff000000);
            context.graphics.fill(x + 1, y - h / 2 + 1, x + w - 1, y - h / 2 + h - 1, 0xff000000 + (this.state ? color : (this.hover ? 0x3a3a3a : 0x444444)));

            if (this.state)
            {
                GuiDraw.drawHorizontalGradientRect(x + 1, y - h / 2 + 1, x + w / 2, y - h / 2 + h - 1, 0x66ffffff, 0x00ffffff);
            }
            else
            {
                GuiDraw.drawHorizontalGradientRect(x + w / 2, y - h / 2 + 1, x + w - 1, y - h / 2 + h - 1, 0x00000000, 0x66000000);
            }

            if (!this.isEnabled())
            {
                context.graphics.fill(x, y - h / 2, x + w, y - h / 2 + h, ColorUtils.HALF_BLACK);
            }

            x += this.state ? w - 2 : 2;

            /* Draw toggle switch */
            context.graphics.fill(x - 4, y - 8, x + 4, y + 8, 0xff000000);
            context.graphics.fill(x - 3, y - 7, x + 3, y + 7, 0xffffffff);
            context.graphics.fill(x - 2, y - 6, x + 3, y + 7, 0xff888888);
            context.graphics.fill(x - 2, y - 6, x + 2, y + 6, 0xffbbbbbb);

            if (!this.isEnabled())
            {
                context.graphics.fill(x - 4, y - 8, x + 4, y + 8, ColorUtils.HALF_BLACK);

                GuiDraw.drawOutlinedIcon(Icons.LOCKED, this.area.ex() - w / 2 - 2, y, 0xffffffff, 0.5F, 0.5F);
            }
        }
    }
}