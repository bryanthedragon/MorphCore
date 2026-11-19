package org.bryanthedragon.morph.client.gui.framework.elements.input;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.config.values.ValueInt;
import org.bryanthedragon.morph.config.values.ValueInt.Subtype;
import org.bryanthedragon.morph.utils.key.Keys;

import net.minecraft.client.Minecraft;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;


public class GuiKeybindElement extends GuiElement
{
    public int keybind;
    public boolean enabled;
    public boolean comboKey;
    public Set<Integer> holding;
    public Consumer<Integer> callback;

    public GuiKeybindElement(Minecraft mc, ValueInt value)
    {
        this(mc, value, null);
    }

    public GuiKeybindElement(Minecraft mc, ValueInt value, Consumer<Integer> callback)
    {
        this(mc, callback == null ? value::set : (integer) -> { value.set(integer); callback.accept(integer); });
        this.setKeybind(value.get());
        this.tooltip(IKey.lang(value.getCommentKey()));

        if (value.getSubtype() == Subtype.COMBOKEY)
        {
            this.comboKey = true;
            this.holding = new LinkedHashSet<Integer>();
        }
    }

    public GuiKeybindElement(Minecraft mc, Consumer<Integer> callback)
    {
        super(mc);

        this.callback = callback;
        this.flex().h(20);
    }

    public void setKeybind(int keybind)
    {
        this.keybind = keybind;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context) && context.mouseButton == 0)
        {
            this.enabled = true;
        }

        return this.area.isInside(context);
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (this.enabled)
        {
            if (this.comboKey && Keys.MODIFIERS.contains(context.keyCode))
            {
                this.holding.add(context.keyCode);
            }
            else
            {
                if (context.keyCode == GLFW.GLFW_KEY_ESCAPE)
                {
                    this.keybind = GLFW.GLFW_KEY_UNKNOWN;
                }
                else
                {
                    if (this.comboKey)
                    {
                        this.keybind = Keys.getComboKeyCode(this.holding.stream().mapToInt(Integer::valueOf).toArray(), context.keyCode);
                        this.holding.clear();
                    }
                    else
                    {
                        this.keybind = context.keyCode;
                    }
                }

                this.enabled = false;

                if (this.callback != null)
                {
                    this.callback.accept(this.keybind);
                }
            }

            return true;
        }

        return false;
    }

    public void checkHolding()
    {
        for (int key : this.holding)
        {
            if (!Keys.isKeyDown(key))
            {
                this.keybind = Keys.getComboKeyCode(this.holding.stream().mapToInt(Integer::valueOf).toArray(), key);
                this.enabled = false;
                this.holding.clear();

                if (this.callback != null)
                {
                    this.callback.accept(this.keybind);
                }

                return;
            }
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.enabled)
        {
            GuiDraw.drawBorder(this.area, 0xff000000 + MorphLib.primaryColor.get());

            int x = this.area.mx();
            int y = this.area.my();
            int a = (int) (Math.sin((context.tick + context.partialTicks) / 2D) * 127.5 + 127.5) << 24;

            context.graphics.fill(x - 1, y - 6, x + 1, y + 6, a + 0xffffff);

            if (this.comboKey)
            {
                this.checkHolding();
            }
        }
        else
        {
            this.area.draw(0xff000000);

            String keyName = Keys.getComboKeyName(this.keybind);
            context.graphics.drawString(this.font, keyName, this.area.mx() - this.font.width(keyName) / 2, this.area.my() - this.font.lineHeight / 2, 0xffffff);
        }

        super.draw(context);
    }
}