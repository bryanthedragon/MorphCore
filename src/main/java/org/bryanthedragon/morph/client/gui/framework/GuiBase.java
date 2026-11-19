package org.bryanthedragon.morph.client.gui.framework;

import org.bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import org.bryanthedragon.morph.client.gui.framework.elements.IViewport;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.IViewportStack;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.utils.Area;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for GUI screens using this framework
 */
@OnlyIn(Dist.CLIENT)
public class GuiBase extends Screen
{
    private static GuiContext current;

    public GuiElement root;
    public GuiContext context = new GuiContext(this);
    public Area viewport = new Area();
    public Minecraft mc;

    public static GuiContext getCurrent()
    {
        return current;
    }

    public GuiBase()
    {
        super(Component.empty());
        current = this.context;
        this.mc = Minecraft.getInstance();
        this.context.mc = this.mc;
        this.context.font = this.mc.font;

        this.root = new GuiRootElement(this.mc);
        this.root.markContainer().flex().relative(this.viewport).wh(1F, 1F);
        this.root.keys().register(IKey.lang("morphlib.gui.keys.list"), GLFW.GLFW_KEY_F9, () -> this.context.keybinds.toggleVisible());
        this.context.keybinds.flex().relative(this.viewport).wh(0.5F, 1F);
    }

    @Nullable
    public static <T> List<T> getCurrentChildren(Class<T> clazz)
    {
        if (GuiBase.getCurrent() != null && GuiBase.getCurrent().screen != null && GuiBase.getCurrent().screen.root != null)
        {
            List<T> childList = GuiBase.getCurrent().screen.root.getChildren(clazz);

            return childList.isEmpty() ? null : childList;
        }

        return null;
    }

    public void updateScreen()
    {
        this.context.tick += 1;
    }

    @Override
    protected void init()
    {
        super.init();
        current = this.context;

        if (!this.context.keybinds.hasParent())
        {
            this.root.add(this.context.keybinds);
        }

        this.viewport.set(0, 0, this.width, this.height);
        this.viewportSet();
        this.context.pushViewport(this.viewport);
        this.root.resize();
        this.context.popViewport();
    }

    protected void viewportSet()
    {}

    @Override
    public void removed()
    {
        super.removed();
        current = null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        this.context.setMouse((int) mouseX, (int) mouseY, mouseButton);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            boolean handled = this.root.mouseClicked(this.context);
            this.context.popViewport();

            if (handled)
            {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        this.context.setMouseWheel((int) mouseX, (int) mouseY, -(int) Math.signum(delta));

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            boolean handled = this.root.mouseScrolled(this.context);
            this.context.popViewport();

            if (handled)
            {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.context.setMouse((int) mouseX, (int) mouseY, button);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            this.root.mouseReleased(this.context);
            this.context.popViewport();

            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char typedChar, int modifiers)
    {
        this.context.setKey(typedChar, 0);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            boolean handled = this.root.keyTyped(this.context);
            this.context.popViewport();

            if (handled)
            {
                return true;
            }
        }

        return super.charTyped(typedChar, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        this.context.setKey((char) 0, keyCode);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            boolean handled = this.root.keyTyped(this.context);
            this.context.popViewport();

            if (handled)
            {
                return true;
            }

            this.context.pushViewport(this.viewport);
            this.keyPressed((char) 0, keyCode);
            this.context.popViewport();
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            this.closeScreen();

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void keyPressed(char typedChar, int keyCode)
    {}

    protected void closeScreen()
    {
        this.mc.setScreen(null);
    }

    public void closeThisScreen()
    {
        this.closeScreen();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.context.graphics = graphics;
        this.context.setMouse(mouseX, mouseY);
        this.context.partialTicks = partialTicks;

        if (this.root.isVisible())
        {
            this.context.reset();
            this.context.pushViewport(this.viewport);
            this.root.draw(this.context);
            this.context.popViewport();
            this.context.drawTooltip();
            this.context.postRenderCallbacks.forEach(element -> element.accept(this.context));
        }
    }

    public static class GuiRootElement extends GuiElement implements IViewport
    {
        public GuiRootElement(Minecraft mc)
        {
            super(mc);
        }

        @Override
        public void apply(IViewportStack stack)
        {
            stack.pushViewport(this.area);
        }

        @Override
        public void unapply(IViewportStack stack)
        {
            stack.popViewport();
        }
    }
}