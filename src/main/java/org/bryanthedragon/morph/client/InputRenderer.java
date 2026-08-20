package bryanthedragon.morph.client;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.events.RenderOverlayEvent;
import bryanthedragon.morph.utils.key.Keys;
import bryanthedragon.morph.utils.matrix.MatrixUtils;
import bryanthedragon.morph.utils.color.ColorUtils;
import bryanthedragon.morph.utils.interpolation.Interpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Mouse renderer
 * 
 * This class is responsible for rendering a mouse pointer on the screen 
 */
@OnlyIn(Dist.CLIENT)
public class InputRenderer
{
    public static boolean disabledForFrame = false;

    private List<PressedKey> pressedKeys = new ArrayList<PressedKey>();
    private float lastQX = 1;
    private float lastQY = 0;
    private float currentQX = 0;
    private float currentQY = 1;
    private long lastDWheelTime;
    private int lastDWheelScroll;

    public static void disable()
    {
        disabledForFrame = true;
    }

    /**
     * Called by ASM
     */
    public static void preRenderOverlay()
    {
        Minecraft mc = Minecraft.getInstance();
        MinecraftForge.EVENT_BUS.post(new RenderOverlayEvent.Pre(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight()));
    }

    /**
     * Called by ASM
     */
    public static void postRenderOverlay()
    {
        Minecraft mc = Minecraft.getInstance();
        MinecraftForge.EVENT_BUS.post(new RenderOverlayEvent.Post(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight()));
    }

    /* Shift -6 and -8 to get it into the center */
    public static void renderMouseButtons(GuiGraphics graphics, int x, int y, int scroll, boolean left, boolean right, boolean middle, boolean isScrolling)
    {
        /* Outline */
        graphics.fill(x - 1, y, x + 13, y + 16, 0xff000000);
        graphics.fill(x, y - 1, x + 12, y + 17, 0xff000000);
        /* Background */
        graphics.fill(x, y + 1, x + 12, y + 15, 0xffffffff);
        graphics.fill(x + 1, y, x + 11, y + 1, 0xffffffff);
        graphics.fill(x + 1, y + 15, x + 11, y + 16, 0xffffffff);
        /* Over outline */
        graphics.fill(x, y + 7, x + 12, y + 8, 0xffeeeeee);

        if (left)
        {
            graphics.fill(x + 1, y, x + 6, y + 7, 0xffcccccc);
            graphics.fill(x, y + 1, x + 1, y + 7, 0xffaaaaaa);
        }

        if (right)
        {
            graphics.fill(x + 6, y, x + 11, y + 7, 0xffaaaaaa);
            graphics.fill(x + 11, y + 1, x + 12, y + 7, 0xff888888);
        }

        if (middle || isScrolling)
        {
            int offset = 0;

            if (isScrolling)
            {
                offset = scroll < 0 ? 1 : -1;
            }

            graphics.fill(x + 4, y, x + 8, y + 6, 0x20000000);
            graphics.fill(x + 5, y + 1 + offset, x + 7, y + 5 + offset, 0xff444444);
            graphics.fill(x + 5, y + 4 + offset, x + 7, y + 5 + offset, 0xff333333);
        }
    }

    public static void renderMouseWheel(GuiGraphics graphics, int x, int y, int scroll, long current)
    {
        int color = MorphLib.primaryColor.get();

        GuiDraw.drawDropShadow(x, y, x + 4, y + 16, 2, ColorUtils.HALF_BLACK + color, color);
        graphics.fill(x, y, x + 4, y + 16, 0xff111111);
        graphics.fill(x + 1, y, x + 3, y + 15, 0xff2a2a2a);

        int offset = (int) ((current % 1000 / 50) % 4);

        if (scroll >= 0)
        {
            offset = 3 - offset;
        }

        for (int i = 0; i < 4; i++)
        {
            graphics.fill(x, y + offset, x + 4, y + offset + 1, 0x88555555);

            y += 4;
        }
    }

    @SubscribeEvent
    public void onDrawEvent(ScreenEvent.Render.Post event)
    {
        if (disabledForFrame)
        {
            disabledForFrame = false;

            return;
        }

        int x = event.getMouseX();
        int y = event.getMouseY();

        GuiGraphics graphics = event.getGuiGraphics();
        this.renderMouse(graphics, x, y);

        if (MorphLib.enableKeystrokeRendering.get())
        {
            this.renderKeys(graphics, x, y);
        }
    }

    /**
     * Draw mouse cursor
     */
    private void renderMouse(GuiGraphics graphics, int x, int y)
    {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 1000);

        if (MorphLib.enableCursorRendering.get())
        {
            Icons.CURSOR.render(x, y);
        }

        if (MorphLib.enableMouseButtonRendering.get())
        {
            long window = Minecraft.getInstance().getWindow().getWindow();
            boolean left = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean right = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            boolean middle = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
            long current = System.currentTimeMillis();
            boolean isScrolling = current - this.lastDWheelTime < 500;
            int scroll = isScrolling ? this.lastDWheelScroll : 0;

            x += 16;
            y += 2;

            if (left || right || middle || isScrolling)
            {
                renderMouseButtons(graphics, x, y, scroll, left, right, middle, isScrolling);
            }

            if (isScrolling)
            {
                x += 16;

                renderMouseWheel(graphics, x, y, scroll, current);
            }
        }

        graphics.pose().popPose();
    }

    @SubscribeEvent
    public void onMouseScrolled(ScreenEvent.MouseScrolled.Post event)
    {
        this.lastDWheelScroll = event.getScrollDelta() > 0 ? 1 : -1;
        this.lastDWheelTime = System.currentTimeMillis();
    }

    /**
     * Render pressed key strokes
     */
    private void renderKeys(GuiGraphics graphics, int mouseX, int mouseY)
    {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        float lqx = Math.round(mouseX / (float) screenWidth);
        float lqy = Math.round(mouseY / (float) screenHeight);
        int mode = MorphLib.keystrokeMode.get();

        if (lqx == this.currentQX && lqy == this.currentQY)
        {
            this.currentQX = this.lastQX;
            this.currentQY = this.lastQY;
        }

        if (mode == 1)
        {
            this.currentQX = 0;
            this.currentQY = 1;
        }
        else if (mode == 2)
        {
            this.currentQX = 1;
            this.currentQY = 1;
        }
        else if (mode == 3)
        {
            this.currentQX = 1;
            this.currentQY = 0;
        }
        else if (mode == 4)
        {
            this.currentQX = 0;
            this.currentQY = 0;
        }

        float qx = this.currentQX;
        float qy = this.currentQY;

        int fy = qy > 0.5F ? 1 : -1;
        int offset = MorphLib.keystrokeOffset.get();
        int mx = offset + (int) (qx * (screenWidth - offset * 2));
        int my = offset + (int) (qy * (screenHeight - 20 - offset * 2));

        Font font = Minecraft.getInstance().font;
        Iterator<PressedKey> it = this.pressedKeys.iterator();

        while (it.hasNext())
        {
            PressedKey key = it.next();

            if (key.expired())
            {
                it.remove();
            }
            else
            {
                int x = mx + (qx < 0.5F ? key.x : -(key.x + key.width + 10));
                int y = my + (int) (Interpolation.EXP_INOUT.interpolate(0, 1, key.getFactor()) * 50 * fy) + (key.i % 2 == 0 ? -1 : 0);

                GuiDraw.drawDropShadow(x, y, x + 10 + key.width, y + 20, 4, 0x44000000, 0);
                graphics.fill(x, y, x + 10 + key.width, y + 20, 0xff000000 + MorphLib.primaryColor.get());
                graphics.drawString(font, key.getLabel(), x + 5, y + 6, 0xffffff, true);
            }
        }

        this.lastQX = lqx;
        this.lastQY = lqy;
    }

    @SubscribeEvent
    public void onKeyPressedInGUI(ScreenEvent.KeyPressed.Post event)
    {
        boolean inputFocused = GuiBase.getCurrent() == null || GuiBase.getCurrent().activeElement == null;

        if (inputFocused)
        {
            int key = event.getKeyCode();

            if (key < GLFW.GLFW_KEY_UNKNOWN || key > GLFW.GLFW_KEY_LAST)
            {
                return;
            }

            PressedKey last = null;
            int offset = -1000;

            for (PressedKey pressed : this.pressedKeys)
            {
                if (pressed.key == key)
                {
                    offset = pressed.increment();
                }
                else if (offset != -1000)
                {
                    pressed.x += offset;
                }

                last = pressed;
            }

            if (offset != -1000)
            {
                return;
            }

            offset = MorphLib.keystrokeOffset.get();
            int x = last == null ? 0 : last.x + last.width + 5;
            PressedKey newKey = new PressedKey(key, x);

            if (newKey.x + newKey.width + offset > event.getScreen().width - offset * 2)
            {
                newKey.x = 0;
            }

            this.pressedKeys.add(newKey);
        }
    }

    /**
     * Release the matrix at the end of frame to avoid messing
     * up matrix capture even more
     */
    @SubscribeEvent
    public void onRenderLast(RenderLevelStageEvent event)
    {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL)
        {
            MatrixUtils.releaseMatrix();
        }
    }

    /**
     * Information about pressed key strokes
     */
    @OnlyIn(Dist.CLIENT)
    public static class PressedKey
    {
        public static int INDEX = 0;

        public int key;
        public long time;
        public int x;

        public String name;
        public int width;
        public int i;
        public int times = 1;

        public PressedKey(int key, int x)
        {
            this.key = key;
            this.time = System.currentTimeMillis();
            this.x = x;

            this.name = Keys.getKeyName(key);
            this.width = Minecraft.getInstance().font.width(this.name);
            this.i = INDEX ++;
        }

        public float getFactor()
        {
            return (System.currentTimeMillis() - this.time - 500) / 1000F;
        }

        public boolean expired()
        {
            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), this.key) == GLFW.GLFW_PRESS)
            {
                this.time = System.currentTimeMillis();
            }

            return System.currentTimeMillis() - this.time > 1500;
        }

        public String getLabel()
        {
            if (this.times > 1)
            {
                return this.name + " (" + this.times + ")";
            }

            return this.name;
        }

        public int increment()
        {
            int lastWidth = this.width;

            this.times ++;
            this.width = Minecraft.getInstance().font.width(this.getLabel());

            return this.width - lastWidth;
        }
    }
}