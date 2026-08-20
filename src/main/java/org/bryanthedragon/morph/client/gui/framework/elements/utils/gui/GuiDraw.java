package bryanthedragon.morph.client.gui.framework.elements.utils.gui;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.utils.Area;
import bryanthedragon.morph.client.gui.utils.Icon;
import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.utils.math.MathUtils;
import bryanthedragon.morph.utils.color.ColorUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Stack;

public class GuiDraw
{
    private final static Stack<Area> scissors = new Stack<Area>();

    private static GuiGraphics graphics()
    {
        GuiContext context = GuiBase.getCurrent();

        return context == null ? null : context.graphics;
    }

    private static void fill(int left, int top, int right, int bottom, int color)
    {
        GuiGraphics graphics = graphics();

        if (graphics != null)
        {
            graphics.fill(left, top, right, bottom, color);
        }
    }

    public static void scissor(int x, int y, int w, int h, GuiContext context)
    {
        scissor(context.globalX(x), context.globalY(y), w, h, context.screen.width, context.screen.height);
    }

    /**
     * Scissor (clip) the screen
     */
    public static void scissor(int x, int y, int w, int h, int sw, int sh)
    {
        Area scissor = scissors.isEmpty() ? null : scissors.peek();

        /* If it was scissored before, then clamp to the bounds of the last one */
        if (scissor != null)
        {
            w += Math.min(x - scissor.x, 0);
            h += Math.min(y - scissor.y, 0);
            x = MathUtils.clamp(x, scissor.x, scissor.ex());
            y = MathUtils.clamp(y, scissor.y, scissor.ey());
            w = MathUtils.clamp(w, 0, scissor.ex() - x);
            h = MathUtils.clamp(h, 0, scissor.ey() - y);
        }

        scissor = new Area(x, y, w, h);
        scissorArea(x, y, w, h, sw, sh);
        scissors.add(scissor);
    }

    private static void scissorArea(int x, int y, int w, int h, int sw, int sh)
    {
        /* Clipping area around scroll area */
        Minecraft mc = Minecraft.getInstance();

        int displayWidth = mc.getWindow().getWidth();
        int displayHeight = mc.getWindow().getHeight();
        float rx = (float) Math.ceil(displayWidth / (double) sw);
        float ry = (float) Math.ceil(displayHeight / (double) sh);

        int xx = (int) (x * rx);
        int yy = (int) (displayHeight - (y + h) * ry);
        int ww = (int) (w * rx);
        int hh = (int) (h * ry);

        RenderSystem.enableScissor(xx, yy, ww, hh);

        if (ww == 0 || hh == 0)
        {
            RenderSystem.enableScissor(0, 0, 1, 1);
        }
    }

    public static void unscissor(GuiContext context)
    {
        unscissor(context.screen.width, context.screen.height);
    }

    public static void unscissor(int sw, int sh)
    {
        scissors.pop();

        if (scissors.isEmpty())
        {
            RenderSystem.disableScissor();
        }
        else
        {
            Area area = scissors.peek();

            scissorArea(area.x, area.y, area.w, area.h, sw, sh);
        }
    }

    public static void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        drawHorizontalGradientRect(left, top, right, bottom, startColor, endColor, 0);
    }

    /**
     * Draws a rectangle with a horizontal gradient between with specified
     * colors, the code is borrowed form drawGradient()
     */
    public static void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel)
    {
        float a1 = (startColor >> 24 & 255) / 255.0F;
        float r1 = (startColor >> 16 & 255) / 255.0F;
        float g1 = (startColor >> 8 & 255) / 255.0F;
        float b1 = (startColor & 255) / 255.0F;
        float a2 = (endColor >> 24 & 255) / 255.0F;
        float r2 = (endColor >> 16 & 255) / 255.0F;
        float g2 = (endColor >> 8 & 255) / 255.0F;
        float b2 = (endColor & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(right, top, zLevel).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(left, top, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left, bottom, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(right, bottom, zLevel).color(r2, g2, b2, a2).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    public static void drawVerticalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        drawVerticalGradientRect(left, top, right, bottom, startColor, endColor, 0);
    }

    /**
     * Draws a rectangle with a vertical gradient between with specified
     * colors
     */
    public static void drawVerticalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel)
    {
        float a1 = (startColor >> 24 & 255) / 255.0F;
        float r1 = (startColor >> 16 & 255) / 255.0F;
        float g1 = (startColor >> 8 & 255) / 255.0F;
        float b1 = (startColor & 255) / 255.0F;
        float a2 = (endColor >> 24 & 255) / 255.0F;
        float r2 = (endColor >> 16 & 255) / 255.0F;
        float g2 = (endColor >> 8 & 255) / 255.0F;
        float b2 = (endColor & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(right, top, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left, top, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left, bottom, zLevel).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(right, bottom, zLevel).color(r2, g2, b2, a2).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH)
    {
        drawBillboard(x, y, u, v, w, h, textureW, textureH, 0);
    }

    /**
     * Draw a textured quad with given UV, dimensions and custom texture size
     */
    public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH, float z)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        drawBillboard(buffer, x, y, u, v, w, h, textureW, textureH, z);
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawBillboard(BufferBuilder buffer, int x, int y, int u, int v, int w, int h, int textureW, int textureH, float z)
    {
        float tw = 1F / textureW;
        float th = 1F / textureH;

        buffer.vertex(x, y + h, z).uv(u * tw, (v + h) * th).endVertex();
        buffer.vertex(x + w, y + h, z).uv((u + w) * tw, (v + h) * th).endVertex();
        buffer.vertex(x + w, y, z).uv((u + w) * tw, v * th).endVertex();
        buffer.vertex(x, y, z).uv(u * tw, v * th).endVertex();
    }

    public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH, int tu, int tv)
    {
        drawBillboard(x, y, u, v, w, h, textureW, textureH, tu, tv, 0);
    }

    /**
     * Draw a textured quad with given UV, dimensions and custom texture size
     */
    public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH, int tu, int tv, float z)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        drawBillboard(buffer, x, y, u, v, w, h, textureW, textureH, tu, tv, z);
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawBillboard(BufferBuilder buffer, int x, int y, int u, int v, int w, int h, int textureW, int textureH, int tu, int tv, float z)
    {
        float tw = 1F / textureW;
        float th = 1F / textureH;

        buffer.vertex(x, y + h, z).uv(u * tw, tv * th).endVertex();
        buffer.vertex(x + w, y + h, z).uv(tu * tw, tv * th).endVertex();
        buffer.vertex(x + w, y, z).uv(tu * tw, v * th).endVertex();
        buffer.vertex(x, y, z).uv(u * tw, v * th).endVertex();
    }

    public static int drawBorder(Area area, int color)
    {
        if (!MorphLib.enableBorders.get())
        {
            area.draw(color);

            return 0;
        }

        area.draw(0xff000000);
        area.draw(color, 1);

        return 1;
    }

    public static void drawOutlineCenter(int x, int y, int offset, int color)
    {
        drawOutlineCenter(x, y, offset, color, 1);
    }

    public static void drawOutlineCenter(int x, int y, int offset, int color, int border)
    {
        drawOutline(x - offset, y - offset, x + offset, y + offset, color, border);
    }

    public static void drawOutline(int left, int top, int right, int bottom, int color)
    {
        drawOutline(left, top, right, bottom, color, 1);
    }

    /**
     * Draw rectangle outline with given border
     */
    public static void drawOutline(int left, int top, int right, int bottom, int color, int border)
    {
        fill(left, top, left + border, bottom, color);
        fill(right - border, top, right, bottom, color);
        fill(left + border, top, right - border, top + border, color);
        fill(left + border, bottom - border, right - border, bottom, color);
    }

    public static void drawOutlinedIcon(Icon icon, int x, int y, int color)
    {
        drawOutlinedIcon(icon, x, y, color, 0F, 0F);
    }

    /**
     * Draw an icon with a black outline
     */
    public static void drawOutlinedIcon(Icon icon, int x, int y, int color, float ax, float ay)
    {
        RenderSystem.setShaderColor(0, 0, 0, 1);
        icon.render(x - 1, y, ax, ay);
        icon.render(x + 1, y, ax, ay);
        icon.render(x, y - 1, ax, ay);
        icon.render(x, y + 1, ax, ay);
        RenderSystem.setShaderColor((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, (color >>> 24) / 255F);
        icon.render(x, y, ax, ay);
    }

    public static void drawLockedArea(GuiElement element)
    {
        drawLockedArea(element, 0);
    }

    /**
     * Generic method for drawing locked (disabled) state of
     * an input field
     */
    public static void drawLockedArea(GuiElement element, int padding)
    {
        if (!element.isEnabled())
        {
            element.area.draw(ColorUtils.HALF_BLACK, padding);

            GuiDraw.drawOutlinedIcon(Icons.LOCKED, element.area.mx(), element.area.my(), 0xffffffff, 0.5F, 0.5F);
        }
    }

    public static void drawDropShadow(int left, int top, int right, int bottom, int offset, int opaque, int shadow)
    {
        left -= offset;
        top -= offset;
        right += offset;
        bottom += offset;

        float a1 = (opaque >> 24 & 255) / 255.0F;
        float r1 = (opaque >> 16 & 255) / 255.0F;
        float g1 = (opaque >> 8 & 255) / 255.0F;
        float b1 = (opaque & 255) / 255.0F;
        float a2 = (shadow >> 24 & 255) / 255.0F;
        float r2 = (shadow >> 16 & 255) / 255.0F;
        float g2 = (shadow >> 8 & 255) / 255.0F;
        float b2 = (shadow & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        /* Draw opaque part */
        buffer.vertex(right - offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left + offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left + offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(right - offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();

        /* Draw top shadow */
        buffer.vertex(right, top, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(left, top, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(left + offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(right - offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();

        /* Draw bottom shadow */
        buffer.vertex(right - offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left + offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left, bottom, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(right, bottom, 0).color(r2, g2, b2, a2).endVertex();

        /* Draw left shadow */
        buffer.vertex(left + offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(left, top, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(left, bottom, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(left + offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();

        /* Draw right shadow */
        buffer.vertex(right, top, 0).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(right - offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(right - offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(right, bottom, 0).color(r2, g2, b2, a2).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    public static void drawDropCircleShadow(int x, int y, int radius, int segments, int opaque, int shadow)
    {
        float a1 = (opaque >> 24 & 255) / 255.0F;
        float r1 = (opaque >> 16 & 255) / 255.0F;
        float g1 = (opaque >> 8 & 255) / 255.0F;
        float b1 = (opaque & 255) / 255.0F;
        float a2 = (shadow >> 24 & 255) / 255.0F;
        float r2 = (shadow >> 16 & 255) / 255.0F;
        float g2 = (shadow >> 8 & 255) / 255.0F;
        float b2 = (shadow & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        buffer.vertex(x, y, 0).color(r1, g1, b1, a1).endVertex();

        for (int i = 0; i <= segments; i ++)
        {
            double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

            buffer.vertex(x - Math.cos(a) * radius, y + Math.sin(a) * radius, 0).color(r2, g2, b2, a2).endVertex();
        }

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    public static void drawDropCircleShadow(int x, int y, int radius, int offset, int segments, int opaque, int shadow)
    {
        if (offset >= radius)
        {
            drawDropCircleShadow(x, y, radius, segments, opaque, shadow);

            return;
        }

        float a1 = (opaque >> 24 & 255) / 255.0F;
        float r1 = (opaque >> 16 & 255) / 255.0F;
        float g1 = (opaque >> 8 & 255) / 255.0F;
        float b1 = (opaque & 255) / 255.0F;
        float a2 = (shadow >> 24 & 255) / 255.0F;
        float r2 = (shadow >> 16 & 255) / 255.0F;
        float g2 = (shadow >> 8 & 255) / 255.0F;
        float b2 = (shadow & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        /* Draw opaque base */
        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(x, y, 0).color(r1, g1, b1, a1).endVertex();

        for (int i = 0; i <= segments; i ++)
        {
            double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

            buffer.vertex(x - Math.cos(a) * offset, y + Math.sin(a) * offset, 0).color(r1, g1, b1, a1).endVertex();
        }

        BufferUploader.drawWithShader(buffer.end());

        /* Draw outer shadow */
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < segments; i ++)
        {
            double alpha1 = i / (double) segments * Math.PI * 2 - Math.PI / 2;
            double alpha2 = (i + 1) / (double) segments * Math.PI * 2 - Math.PI / 2;

            buffer.vertex(x - Math.cos(alpha2) * offset, y + Math.sin(alpha2) * offset, 0).color(r1, g1, b1, a1).endVertex();
            buffer.vertex(x - Math.cos(alpha1) * offset, y + Math.sin(alpha1) * offset, 0).color(r1, g1, b1, a1).endVertex();
            buffer.vertex(x - Math.cos(alpha1) * radius, y + Math.sin(alpha1) * radius, 0).color(r2, g2, b2, a2).endVertex();
            buffer.vertex(x - Math.cos(alpha2) * radius, y + Math.sin(alpha2) * radius, 0).color(r2, g2, b2, a2).endVertex();
        }

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    public static int drawMultiText(Font font, String text, int x, int y, int color, int width)
    {
        return drawMultiText(font, text, x, y, color, width, 12);
    }

    public static int drawMultiText(Font font, String text, int x, int y, int color, int width, int lineHeight)
    {
        return drawMultiText(font, text, x, y, color, width, lineHeight, 0F, 0F);
    }

    public static int drawMultiText(Font font, String text, int x, int y, int color, int width, int lineHeight, float ax, float ay)
    {
        List<FormattedCharSequence> list = font.split(Component.literal(text), width);
        int h = (lineHeight * (list.size() - 1)) + font.lineHeight;
        GuiGraphics graphics = graphics();

        y -= h * ay;

        if (graphics != null)
        {
            for (FormattedCharSequence line : list)
            {
                graphics.drawString(font, line, (int) (x + (width - font.width(line)) * ax), y, color, true);

                y += lineHeight;
            }
        }

        return h;
    }

    public static void drawTextBackground(Font font, String text, int x, int y, int color, int background)
    {
        drawTextBackground(font, text, x, y, color, background, 3);
    }

    public static void drawTextBackground(Font font, String text, int x, int y, int color, int background, int offset)
    {
        drawTextBackground(font, text, x, y, color, background, offset, true);
    }

    public static void drawTextBackground(Font font, String text, int x, int y, int color, int background, int offset, boolean shadow)
    {
        int a = background >> 24 & 0xff;
        GuiGraphics graphics = graphics();

        if (graphics != null && a != 0)
        {
            graphics.fill(x - offset, y - offset, x + font.width(text) + offset, y + font.lineHeight + offset, background);
        }

        if (graphics != null)
        {
            graphics.drawString(font, text, x, y, color, shadow);
        }
    }

    public static void drawCustomBackground(int x, int y, int width, int height)
    {
        ResourceLocation background = MorphLib.backgroundImage.get();
        int color = MorphLib.backgroundColor.get();

        GuiGraphics graphics = graphics();

        if (graphics == null)
        {
            return;
        }

        graphics.pose().pushPose();

        if (background == null)
        {
            graphics.fill(x, y, x + width, y + height, color);
        }
        else
        {
            RenderSystem.setShaderTexture(0, background);
            RenderSystem.setShaderColor((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, (color >>> 24) / 255F);
            GuiDraw.drawBillboard(x, y, 0, 0, width, height, width, height);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        graphics.pose().popPose();
    }

    public static void drawRepeatBillboard(int x, int y, int w, int h, int u, int v, int tileW, int tileH, int tw, int th)
    {
        int countX = ((w - 1) / tileW) + 1;
        int countY = ((h - 1) / tileH) + 1;
        int fillerX = w - (countX - 1) * tileW;
        int fillerY = h - (countY - 1) * tileH;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0, c = countX * countY; i < c; i ++)
        {
            int ix = i % countX;
            int iy = i / countX;
            int xx = x + ix * tileW;
            int yy = y + iy * tileH;
            int xw = ix == countX - 1 ? fillerX : tileW;
            int yh = iy == countY - 1 ? fillerY : tileH;

            drawBillboard(buffer, xx, yy, u, v, xw, yh, tw, th, 0);
        }

        BufferUploader.drawWithShader(buffer.end());
    }
}