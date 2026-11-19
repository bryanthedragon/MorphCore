package org.bryanthedragon.morph.client.gui.framework.elements.input;

import com.google.common.base.Predicate;

import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.text.ITextColoring;
import org.bryanthedragon.morph.client.gui.responder.GuiResponder;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.config.values.ValueString;
import org.bryanthedragon.morph.utils.pattern.PatternUtils.Patterns;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

/**
 * GUI text element
 * 
 * This element is a wrapper for the text field class
 */
@OnlyIn(Dist.CLIENT)
public class GuiTextElement extends GuiBaseTextElement implements GuiResponder, ITextColoring
{
    public static final Predicate<String> FILENAME_PREDICATE = (s) -> Patterns.FILENAME.matcher(s).find();

    public Consumer<String> callback;
    private boolean bordered = true;

    public GuiTextElement(Minecraft mc, ValueString value)
    {
        this(mc, value, null);
    }

    public GuiTextElement(Minecraft mc, ValueString value, Consumer<String> callback)
    {
        this(mc, callback == null ? value::set : (string) ->
        {
            value.set(string);
            callback.accept(string);
        });
        this.setText(value.get());
        this.tooltip(IKey.lang(value.getConfig().getValueLabelKey(value)));
    }

    public GuiTextElement(Minecraft mc, int maxLength, Consumer<String> callback)
    {
        this(mc, callback);
        this.field.setMaxLength(maxLength);
    }

    public GuiTextElement(Minecraft mc, Consumer<String> callback)
    {
        super(mc);

        this.field.setResponder(value -> this.setEntryValue(0, value));
        this.callback = callback;

        this.flex().h(20);
    }

    public GuiTextElement filename()
    {
        return this.validator(FILENAME_PREDICATE);
    }

    public GuiTextElement validator(Predicate<String> validator)
    {
        this.field.setFilter(validator::apply);

        return this;
    }

    public GuiTextElement background(boolean background)
    {
        this.bordered = background;
        this.field.setBordered(background);
        this.resize();

        return this;
    }

    public void setText(String text)
    {
        if (text == null)
        {
            text = "";
        }

        this.field.setValue(text);
        this.field.moveCursorToStart();
    }

    public void setEntryValue(int id, boolean value)
    {}

    public void setEntryValue(int id, float value)
    {}

    public void setEntryValue(int id, String value)
    {
        if (this.callback != null)
        {
            this.callback.accept(value);
        }
    }

    public void setColor(int color, boolean shadow)
    {
        this.field.setTextColor(color);
    }

    public void resize()
    {
        super.resize();

        this.field.setX(this.area.x + 1);
        this.field.setY(this.area.y + 1);
        this.field.setWidth(this.area.w - 2);
        this.field.setHeight(this.area.h - 2);

        if (!this.bordered)
        {
            this.field.setX(this.field.getX() + 4);
            this.field.setY(this.field.getY() + ((this.area.h - this.font.lineHeight) / 2));
            this.field.setWidth(this.field.getWidth() - 8);
            this.field.setHeight(this.font.lineHeight);
        }
    }

    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        boolean wasFocused = this.field.isFocused();

        this.field.mouseClicked(context.mouseX, context.mouseY, context.mouseButton);

        if (context.mouseButton == 0 && !this.field.isMouseOver(context.mouseX, context.mouseY))
        {
            this.field.setFocused(false);
        }

        if (wasFocused != this.field.isFocused())
        {
            context.focus(wasFocused ? null : this);
        }

        return context.mouseButton == 0 && this.area.isInside(context);
    }

    public boolean keyTyped(GuiContext context)
    {
        if (this.isFocused())
        {
            if (context.keyCode == GLFW.GLFW_KEY_TAB)
            {
                context.focus(this, -1, Screen.hasShiftDown() ? -1 : 1);

                return true;
            }
            else if (context.keyCode == GLFW.GLFW_KEY_ESCAPE)
            {
                context.unfocus();

                return true;
            }
        }

        return this.handleTextInput(context) || super.keyTyped(context);
    }

    public void draw(GuiContext context)
    {
        this.field.render(context.graphics, context.mouseX, context.mouseY, context.partialTicks);

        GuiDraw.drawLockedArea(this);

        super.draw(context);
    }
}