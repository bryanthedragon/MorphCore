package bryanthedragon.morph.client.gui.framework.elements.buttons;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.client.gui.framework.elements.utils.text.ITextColoring;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiButtonElement extends GuiClickElement<GuiButtonElement> implements ITextColoring
{
    public IKey label;

    public int textColor = 0xffffff;
    public boolean textShadow = true;

    public boolean custom;
    public int customColor;
    public boolean background = true;

    public GuiButtonElement(Minecraft mc, IKey label, Consumer<GuiButtonElement> callback)
    {
        super(mc, callback);

        this.label = label;
        this.flex().h(20);
    }

    public GuiButtonElement color(int color)
    {
        this.custom = true;
        this.customColor = color & 0xffffff;

        return this;
    }

    public GuiButtonElement textColor(int color, boolean shadow)
    {
        this.textColor = color;
        this.textShadow = shadow;

        return this;
    }

    public GuiButtonElement background(boolean background)
    {
        this.background = background;

        return this;
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.textColor = color;
        this.textShadow = shadow;
    }

    @Override
    protected GuiButtonElement get()
    {
        return this;
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        int color = 0xff000000 + (this.custom ? this.customColor : MorphLib.primaryColor.get());

        if (this.hover)
        {
            color = ColorUtils.multiplyColor(color, 0.85F);
        }

        if (this.background)
        {
            GuiDraw.drawBorder(this.area, color);
        }

        String label = this.label.get();
        int x = this.area.mx(this.font.width(label));
        int y = this.area.my(this.font.lineHeight - 1);

        context.graphics.drawString(this.font, label, x, y, ColorUtils.multiplyColor(this.textColor, this.hover ? 0.9F : 1F), this.textShadow);

        GuiDraw.drawLockedArea(this);
    }
}