package bryanthedragon.morph.client.gui.framework.elements.modals;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.utils.math.MathUtils;
import bryanthedragon.morph.utils.time.Timer;
import bryanthedragon.morph.utils.color.Color;
import bryanthedragon.morph.utils.interpolation.Interpolations;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class GuiPopUpModal extends GuiModal
{
    private Color backgroundColorDefault = new Color(0F, 0F, 0F, 0F);
    private Color textColorDefault = new Color(1F, 1F, 1F, 0F);
    private float shadowAlphaDefault = 0.26666668F;
    private float alphaDefault = 1F;

    private boolean init = false;
    private int x0;
    private int y0;
    private Timer timer;

    /* Colours */
    private float shadowAlpha;
    private float textAlpha;
    private float backgroundAlpha;

    private Color shadowColor;
    private Color backgroundColor;
    private Color textColor;

    /**
     * fade duration in milliseconds
     */
    private int duration = 300;

    public GuiPopUpModal(Minecraft mc, IKey label)
    {
        super(mc, label);

        this.defaultColors();
    }

    /**
     * Set the duration for the fading animation if it hadn't already begun
     *
     * @param duration in milliseconds
     */
    public void setFadeDuration(int duration)
    {
        if (this.timer == null)
        {
            this.duration = duration;
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        if (!this.init)
        {
            this.x0 = context.mouseX() - this.area.w / 4;
            this.y0 = context.mouseY() - this.area.h / 2;

            this.x0 = this.x0 < 0 ? 0 : this.x0;
            this.y0 = this.y0 < 0 ? 0 : this.y0;

            if (context.screen.width < this.x0 + this.area.w)
            {
                this.x0 = context.screen.width - this.area.w;
            }

            if (context.screen.height < this.y0 + this.area.h)
            {
                this.y0 = context.screen.height - this.area.h;
            }

            this.init = true;
        }

        this.area.x = this.x0;
        this.area.y = this.y0;

        int shadowAlpha = (int)(this.shadowAlpha * 255F) << 24;
        int textAlpha = (int)(this.textAlpha * 255F) << 24;
        int backgroundAlpha = (int)(this.backgroundAlpha * 255F) << 24;

        GuiDraw.drawDropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 6, shadowAlpha + this.shadowColor.getRGBColor(), this.shadowColor.getRGBColor());
        context.graphics.fill(this.area.x, this.area.y, this.area.ex(), this.area.ey(), backgroundAlpha + this.backgroundColor.getRGBAColor());

        this.y = 0;

        List<FormattedCharSequence> lines = this.font.split(Component.literal(this.label.get()), this.area.w - 20);
        for (FormattedCharSequence line : lines)
        {
            context.graphics.drawString(this.font, line, this.area.x + 10, this.area.y + 10 + this.y, textAlpha + this.textColor.getRGBAColor(), true);

            this.y += 11;
        }

        if (!this.area.isInside(context))
        {
            if (this.timer == null)
            {
                this.timer = new Timer(this.duration);

                this.timer.mark();
            }

            float x = ((float)this.duration - this.timer.getRemaining()) / this.duration;

            this.backgroundAlpha = this.fadeAlpha(this.alphaDefault, 0F, x);
            this.textAlpha = this.fadeAlpha(this.alphaDefault, 0F, x);
            this.shadowAlpha = this.fadeAlpha(this.shadowAlphaDefault, 0F,  MathUtils.clamp(x * 1.7F, 0, 1));

            if(this.timer.check())
            {
                context.postRenderCallbacks.add((c) ->
                {
                    this.removeFromParent();
                });
            }
        }
        else if (this.timer != null && !this.timer.check()) //if the mouse came back before the timer ended
        {
            this.timer = null;

            this.defaultColors();
        }

    }

    public void defaultColors()
    {
        this.shadowAlpha = this.shadowAlphaDefault;
        this.textAlpha = this.alphaDefault;
        this.backgroundAlpha = this.alphaDefault;

        this.shadowColor = new Color(MorphLib.primaryColor.get());
        this.backgroundColor = this.backgroundColorDefault.copy();
        this.textColor = this.textColorDefault.copy();
    }

    private float fadeAlpha(float a, float b, float x)
    {
        return Interpolations.lerp(a, b, x);
    }
}
