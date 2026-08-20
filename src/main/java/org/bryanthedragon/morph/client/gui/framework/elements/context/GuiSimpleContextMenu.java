package bryanthedragon.morph.client.gui.framework.elements.context;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.list.GuiListElement;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.client.gui.utils.Icon;
import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.utils.color.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiSimpleContextMenu extends GuiContextMenu
{
    public GuiListElement<Action> actions;

    private boolean shadow;

    public GuiSimpleContextMenu(Minecraft mc)
    {
        super(mc);

        this.actions = new GuiActionListElement(mc, (action) ->
        {
            if (action.get(0).runnable != null)
            {
                action.get(0).runnable.run();
            }

            this.removeFromParent();
        });

        this.actions.flex().relative(this).w(1, 0).h(1, 0);
        this.add(this.actions);
    }

    public GuiSimpleContextMenu shadow()
    {
        this.shadow = true;

        return this;
    }

    public GuiSimpleContextMenu action(IKey label, Runnable runnable)
    {
        return this.action(Icons.NONE, label, runnable);
    }

    public GuiSimpleContextMenu action(Icon icon, IKey label, Runnable runnable)
    {
        if (icon == null || label == null)
        {
            return this;
        }

        return this.action(new Action(icon, label, runnable));
    }

    public GuiSimpleContextMenu action(Icon icon, IKey label, Runnable runnable, int color)
    {
        if (icon == null || label == null)
        {
            return this;
        }

        return this.action(new ColorfulAction(icon, label, runnable, color));
    }

    public GuiSimpleContextMenu action(Action action)
    {
        this.actions.add(action);

        return this;
    }

    @Override
    public void setMouse(GuiContext context)
    {
        int w = 100;

        for (Action action : this.actions.getList())
        {
            w = Math.max(action.getWidth(context.font), w);
        }

        Supplier<Float> h = () ->
        {
            return (float) Math.min(this.actions.scroll.scrollSize, context.screen.height - 10);
        };

        this.flex().set(context.mouseX(), context.mouseY(), w, 0).h(h).bounds(context.screen.root, 5);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.shadow)
        {
            int color = MorphLib.primaryColor.get();

            GuiDraw.drawDropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 10, 0x44000000 + color, color);
        }

        super.draw(context);
    }

    public static class GuiActionListElement extends GuiListElement<Action>
    {
        public GuiActionListElement(Minecraft mc, Consumer<List<Action>> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 20;
        }

        @Override
        public void drawListElement(Action element, int i, int x, int y, boolean hover, boolean selected)
        {
            int h = this.scroll.scrollItemSize;
            GuiContext context = GuiBase.getCurrent();

            element.draw(context.font, context.graphics, x, y, this.scroll.w, h, hover, selected);
        }
    }

    public static class Action
    {
        public Icon icon;
        public IKey label;
        public Runnable runnable;

        public Action(Icon icon, IKey label, Runnable runnable)
        {
            this.icon = icon;
            this.label = label;
            this.runnable = runnable;
        }

        public int getWidth(Font font)
        {
            return 28 + font.width(this.label.get());
        }

        public void draw(Font font, GuiGraphics graphics, int x, int y, int w, int h, boolean hover, boolean selected)
        {
            this.drawBackground(graphics, x, y, w, h, hover, selected);

            RenderSystem.setShaderColor(1, 1, 1, 1);
            this.icon.render(x + 2, y + h / 2, 0, 0.5F);
            graphics.drawString(font, this.label.get(), x + 22, y + (h - font.lineHeight) / 2 + 1, 0xffffff);
        }

        protected void drawBackground(GuiGraphics graphics, int x, int y, int w, int h, boolean hover, boolean selected)
        {
            if (hover)
            {
                graphics.fill(x, y, x + w, y + h, ColorUtils.HALF_BLACK + MorphLib.primaryColor.get());
            }
        }
    }

    public static class ColorfulAction extends Action
    {
        public int color;

        public ColorfulAction(Icon icon, IKey label, Runnable runnable, int color)
        {
            super(icon, label, runnable);

            this.color = color;
        }

        @Override
        protected void drawBackground(GuiGraphics graphics, int x, int y, int w, int h, boolean hover, boolean selected)
        {
            super.drawBackground(graphics, x, y, w, h, hover, selected);

            graphics.fill(x, y, x + 2, y + h, 0xff000000 + this.color);
            GuiDraw.drawHorizontalGradientRect(x + 2, y, x + 24, y + h, 0x44000000 + this.color, this.color);
        }
    }
}