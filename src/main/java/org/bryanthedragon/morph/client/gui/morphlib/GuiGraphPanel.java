package org.bryanthedragon.morph.client.gui.morphlib;

import org.bryanthedragon.morph.client.gui.framework.elements.buttons.GuiIconElement;
import org.bryanthedragon.morph.client.gui.framework.elements.input.GuiTextElement;
import org.bryanthedragon.morph.client.gui.framework.elements.input.GuiTrackpadElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiCanvas;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.utils.GuiUtils;
import org.bryanthedragon.morph.client.gui.utils.Icons;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.math.IValue;
import org.bryanthedragon.morph.math.MathBuilder;
import org.bryanthedragon.morph.math.Variable;
import org.bryanthedragon.morph.utils.vector.Direction;
import org.bryanthedragon.morph.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

public class GuiGraphPanel extends GuiDashboardPanel<GuiDashboard>
{
    public GuiGraphCanvas canvas;
    public GuiTextElement expression;
    public GuiIconElement help;

    public GuiGraphPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.canvas = new GuiGraphCanvas(mc);
        this.expression = new GuiTextElement(mc, 10000, this.canvas::parseExpression);
        this.help = new GuiIconElement(mc, Icons.HELP, (b) -> GuiUtils.openWebLink("https://github.com/mchorse/aperture/wiki/Math-Expressions"));
        this.help.tooltip(IKey.lang("morphlib.gui.graph.help"), Direction.TOP);

        String first = "sin(x)";

        this.expression.setText(first);
        this.canvas.parseExpression(first);

        this.expression.flex().relative(this).x(10).y(1F, -30).w(1F, -20).h(20);
        this.canvas.flex().relative(this).wh(1F, 1F);
        this.help.flex().relative(this.expression).x(1F, -19).y(1).wh(18, 18);

        this.expression.add(this.help);
        this.add(this.canvas, this.expression);
    }

    @Override
    public boolean isClientSideOnly()
    {
        return true;
    }

    public static class GuiGraphCanvas extends GuiCanvas
    {
        private MathBuilder builder;
        private Variable x;
        private boolean first = true;

        public IValue expression;

        public GuiGraphCanvas(Minecraft mc)
        {
            super(mc);

            this.builder = new MathBuilder();
            this.builder.register(this.x = new Variable("x", 0));

            this.scaleY.inverse = true;
        }

        public void parseExpression(String expression)
        {
            try
            {
                this.expression = this.builder.parse(expression);
            }
            catch (Exception e)
            {
                this.expression = null;
            }
        }

        @Override
        public void resize()
        {
            super.resize();

            if (this.first)
            {
                this.scaleX.view(-10, 10);
                this.scaleX.calculateMultiplier();
                this.scaleY.view(-10, 10);
                this.scaleY.calculateMultiplier();

                this.first = false;
            }
        }

        @Override
        protected void drawCanvas(GuiContext context)
        {
            this.area.draw(ColorUtils.HALF_BLACK);

            this.drawVerticalGrid(context);
            this.drawHorizontalGridAndGraph(context);
        }

        private void drawVerticalGrid(GuiContext context)
        {
            /* Draw vertical grid */
            int ty = (int) this.scaleY.from(this.area.ey());
            int by = (int) this.scaleY.from(this.area.y - 12);

            int min = Math.min(ty, by) - 1;
            int max = Math.max(ty, by) + 1;
            int mult = this.scaleY.getMult();

            min -= min % mult + mult;
            max -= max % mult - mult;

            for (int j = 0, c = (max - min) / mult; j < c; j++)
            {
                int y = (int) this.scaleY.to(min + j * mult);

                if (y >= this.area.ey())
                {
                    continue;
                }

                context.graphics.fill(this.area.x, y, this.area.ex(), y + 1, 0x44ffffff);
                context.graphics.drawString(context.font, String.valueOf(min + j * mult), this.area.x + 4, y + 4, 0xffffff, false);
            }
        }

        private void drawHorizontalGridAndGraph(GuiContext context)
        {
            /* Draw scaling grid */
            int tx = (int) this.scaleX.from(this.area.ex());
            int bx = (int) this.scaleX.from(this.area.x);

            int min = Math.min(tx, bx) - 1;
            int max = Math.max(tx, bx) + 1;
            int mult = this.scaleX.getMult();

            min -= min % mult + mult;
            max -= max % mult - mult;

            for (int j = 0, c = (max - min) / mult; j < c; j++)
            {
                int x = (int) this.scaleX.to(min + j * mult);

                if (x >= this.area.ex())
                {
                    break;
                }

                context.graphics.fill(x, this.area.y, x + 1, this.area.ey(), 0x44ffffff);
                context.graphics.drawString(context.font, String.valueOf(min + j * mult), x + 4, this.area.y + 4, 0xffffff, false);
            }

            if (this.expression == null)
            {
                return;
            }

            if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS && !context.isFocused())
            {
                int mouseX = context.mouseX;
                double x = this.scaleX.from(mouseX);

                this.x.set(x);

                double y = this.expression.get().doubleValue();
                int y1 = context.mouseY;
                int y2 = (int) this.scaleY.to(y) + 1;
                boolean isNaN = Double.isNaN(y);

                if (y1 < y2)
                {
                    y1 -= 12;
                }

                String coordinate = "(" + GuiTrackpadElement.FORMAT.format(x) + ", " + (isNaN ? "undefined" : GuiTrackpadElement.FORMAT.format(y)) + ")";

                if (!isNaN)
                {
                    context.graphics.fill(mouseX, Math.min(y1, y2), mouseX + 1, Math.max(y1, y2), 0xff57f52a);
                }

                int y3 = y1 < y2 ? y1 : y1 - 12;
                int w = context.font.width(coordinate);

                mouseX += 1;

                context.graphics.fill(mouseX, y3, mouseX + w + 4, y3 + 12, 0xffffffff);
                context.graphics.drawString(context.font, coordinate, mouseX + 2, y3 + 2, 0, false);
            }

            int sub = 40;
            int gap = (max - min) * sub;

            for (int j = 1; j < gap; j++)
            {
                double previous = min + (j - 1) / (double) sub;
                double current = min + j / (double) sub;

                this.x.set(previous);
                double y1 = this.expression.get().doubleValue();

                this.x.set(current);
                double y2 = this.expression.get().doubleValue();

                double fx1 = this.scaleX.to(previous);
                double fy1 = this.scaleY.to(y1);
                double fx2 = this.scaleX.to(current);
                double fy2 = this.scaleY.to(y2);

                if (Double.isFinite(fy1) && Double.isFinite(fy2))
                {
                    int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(fx2 - fx1), Math.abs(fy2 - fy1))));

                    for (int step = 0; step <= steps; step++)
                    {
                        double part = step / (double) steps;
                        int px = (int) Math.round(fx1 + (fx2 - fx1) * part);
                        int py = (int) Math.round(fy1 + (fy2 - fy1) * part);
                        context.graphics.fill(px - 1, py - 1, px + 2, py + 2, 0xff0080ff);
                    }
                }

            }
        }
    }
}
