package bryanthedragon.morph.client.gui.framework.elements.input;

import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.framework.elements.IFocusedGuiElement;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.utils.color.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public abstract class GuiBaseTextElement extends GuiElement implements IFocusedGuiElement
{
    public EditBox field;

    public GuiBaseTextElement(Minecraft mc)
    {
        super(mc);

        this.field = new EditBox(this.font, 0, 0, 0, 0, Component.empty());
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        this.field.active = enabled;
        this.field.setEditable(enabled);
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        this.field.setVisible(visible);
    }

    public void setTextColor(Color color)
    {
        this.field.setTextColor(color.getRGBAColor());
    }

    public void setTextColor(int color)
    {
        this.field.setTextColor(color);
    }

    public void setText(String textIn)
    {
        this.field.setValue(textIn == null ? "" : textIn);
    }

    @Override
    public boolean isFocused()
    {
        return this.field.isFocused();
    }

    @Override
    public void focus(GuiContext context)
    {
        this.field.setFocused(true);
    }

    @Override
    public void unfocus(GuiContext context)
    {
        this.field.setFocused(false);
    }

    @Override
    public void selectAll(GuiContext context)
    {
        this.field.setCursorPosition(0);
        this.field.setHighlightPos(this.field.getValue().length());
    }

    @Override
    public void unselect(GuiContext context)
    {
        this.field.setHighlightPos(this.field.getCursorPosition());
    }

    protected boolean handleTextInput(GuiContext context)
    {
        if (context.keyCode != 0)
        {
            return this.field.keyPressed(context.keyCode, 0, 0);
        }

        return context.typedChar != 0 && this.field.charTyped(context.typedChar, 0);
    }
}