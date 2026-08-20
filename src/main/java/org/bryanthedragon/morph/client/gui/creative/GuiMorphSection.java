package bryanthedragon.morph.client.gui.creative;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.framework.elements.context.GuiContextMenu;
import bryanthedragon.morph.client.gui.framework.elements.context.GuiSimpleContextMenu;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.client.gui.utils.Area;
import bryanthedragon.morph.client.gui.utils.Icon;
import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.utils.key.Keys;
import bryanthedragon.morph.utils.color.ColorUtils;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.creative.categories.MorphCategory;
import bryanthedragon.morph.api.creative.categories.UserCategory;
import bryanthedragon.morph.api.creative.sections.MorphSection;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.client.gui.GuiMorphs;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.nbt.CompoundTag;

import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class GuiMorphSection extends GuiElement
{
    public static final int HEADER_HEIGHT = 20;
    public static final int CATEGORY_HEIGHT = 16;

    public GuiCreativeMorphsList parent;
    public MorphSection section;
    public Consumer<GuiMorphSection> callback;

    public int cellWidth = 55;
    public int cellHeight = 70;
    public boolean last;
    public boolean favorite;

    public AbstractMorph morph;
    public MorphCategory category;

    protected AbstractMorph hoverMorph;
    protected MorphCategory hoverCategory;

    public int height;

    private String filter = "";

    public static String getMorphCommand(AbstractMorph morph)
    {
        if (morph != null)
        {
            CompoundTag nbt = morph.toNBT();

            nbt.remove("Name");

            return "/morph @p " + morph.name + " " + nbt.toString();
        }

        return "";
    }

    public GuiMorphSection(Minecraft mc, GuiCreativeMorphsList parent, MorphSection section, Consumer<GuiMorphSection> callback)
    {
        super(mc);

        this.parent = parent;
        this.section = section;
        this.callback = callback;
    }

    public GuiMorphSection size(int w, int h)
    {
        this.cellWidth = w;
        this.cellHeight = h;

        return this;
    }

    public void set(AbstractMorph morph, MorphCategory category)
    {
        this.morph = morph;
        this.category = category;
    }

    public void pick(AbstractMorph morph, MorphCategory category)
    {
        this.set(morph, category);

        if (this.callback != null)
        {
            this.callback.accept(this);
        }
    }

    public void reset()
    {
        this.set(null, null);
    }

    /* Searching methods */

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public boolean noFilter()
    {
        return this.filter.isEmpty() && !this.favorite;
    }

    public boolean isMatching(AbstractMorph morph)
    {
        if (this.favorite)
        {
            return morph.favorite;
        }

        if (this.filter.isEmpty())
        {
            return true;
        }

        return morph.name.toLowerCase().contains(this.filter) || morph.getDisplayName().toLowerCase().contains(this.filter);
    }

    public void calculateXY(GuiMorphs morphs)
    {
        int j = 0;

        for (AbstractMorph morph : this.category.getMorphs())
        {
            if (morph == this.morph)
            {
                int row = this.getPerRow();

                morphs.x = j % row;
                morphs.y = j / row;

                return;
            }

            if (this.isMatching(morph))
            {
                j ++;
            }
        }
    }

    public AbstractMorph getMorphAt(GuiMorphs morphs)
    {
        int row = this.getPerRow();
        int size = this.category.getMorphs().size();

        /* Shortcuts */
        if (morphs.y < 0 || size == 0)
        {
            return null;
        }
        else if (morphs.y < size / row + 1)
        {
            int i = morphs.x + morphs.y * row;

            if (i >= size)
            {
                return this.category.getMorphs().get(size - 1);
            }
        }

        /* Find the actual morph */
        int j = 0;

        if (morphs.x < 0) morphs.x = row - 1;
        if (morphs.x > row - 1) morphs.x = 0;

        for (AbstractMorph morph : this.category.getMorphs())
        {
            if (this.isMatching(morph))
            {
                if (j % row == morphs.x && j / row == morphs.y)
                {
                    return morph;
                }

                j ++;
            }
        }

        return null;
    }

    public int getY(AbstractMorph selected)
    {
        if (this.section.categories.isEmpty())
        {
            return 0;
        }

        int y = HEADER_HEIGHT;
        int row = this.getPerRow();

        for (MorphCategory category : this.section.categories)
        {
            int count = this.getMorphsSize(category);

            if (category.isHidden() || (count == 0 && !this.noFilter()))
            {
                continue;
            }

            y += CATEGORY_HEIGHT + 5;

            for (int i = 0, j = 0; i < category.getMorphs().size(); i ++)
            {
                AbstractMorph morph = category.getMorphs().get(i);

                if (!this.isMatching(morph))
                {
                    continue;
                }

                if (j != 0 && j % row == 0)
                {
                    y += this.cellHeight;
                }

                if (morph == selected)
                {
                    return y;
                }

                j ++;
            }

            y += this.cellHeight + 5;
        }

        return -1;
    }

    /* Calculation methods */

    public int getMorphsSize(MorphCategory category)
    {
        if (this.noFilter())
        {
            return category.getMorphs().size();
        }

        int count = 0;

        for (AbstractMorph morph : category.getMorphs())
        {
            count += this.isMatching(morph) ? 1 : 0;
        }

        return count;
    }

    public int getPerRow()
    {
        return Math.max(this.area.w / this.cellWidth, 1);
    }

    public int getCategoryHeight(MorphCategory category)
    {
        return this.getCategoryHeight(this.getMorphsSize(category));
    }

    public int getCategoryHeight(int given)
    {
        int size = Math.max(given, 1);

        return (int) Math.ceil(size / (float) this.getPerRow()) * this.cellHeight;
    }

    public int getFullHeight()
    {
        int h = HEADER_HEIGHT + (this.last ? 30 : 0);

        if (!this.section.hidden)
        {
            int visibleCategories = 0;

            for (MorphCategory category : this.section.categories)
            {
                int count = this.getMorphsSize(category);

                h += CATEGORY_HEIGHT + 5;

                if (category.isHidden() || (count == 0 && !this.noFilter()))
                {
                    continue;
                }

                h += this.getCategoryHeight(category) + 5;
                visibleCategories += 1;
            }

            if (visibleCategories > 0)
            {
                h -= 5;
            }
        }

        return h;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        boolean result = false;

        if (this.area.isInside(context) && !this.section.categories.isEmpty())
        {
            if (context.mouseY - this.area.y < HEADER_HEIGHT && context.mouseButton == 0)
            {
                this.section.hidden = !this.section.hidden;

                return true;
            }

            int x = context.mouseX - this.area.x;
            int y = context.mouseY - this.area.y - HEADER_HEIGHT;
            int row = this.getPerRow();

            category:
            for (MorphCategory category : this.section.categories)
            {
                int count = this.getMorphsSize(category);

                if (y < CATEGORY_HEIGHT && context.mouseButton == 0)
                {
                    category.hidden = !category.hidden;

                    return true;
                }

                y -= CATEGORY_HEIGHT + 5;

                if (category.isHidden() || (count == 0 && !this.noFilter()))
                {
                    continue;
                }

                int ix = (int) (x / (this.area.w / (float) row));
                int iy = y / this.cellHeight;
                int i = ix + (y < 0 ? -1 : iy) * row;

                if (i >= 0 && i < count)
                {
                    int real = category.getMorphs().size();

                    if (count == real)
                    {
                        this.pick(category.getMorphs().get(i), category);

                        result = true;

                        break;
                    }
                    else
                    {
                        for (int j = 0, k = -1; j < real; j ++)
                        {
                            AbstractMorph morph = category.getMorphs().get(j);

                            if (this.isMatching(morph))
                            {
                                k ++;
                            }

                            if (i == k)
                            {
                                this.pick(morph, category);

                                result = true;
                                break category;
                            }
                        }
                    }
                }

                y -= this.getCategoryHeight(count) + 5;
            }

            if (!result)
            {
                this.pick(null, null);
            }
        }

        return super.mouseClicked(context) || result;
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        if (this.parent == null)
        {
            return super.createContextMenu(context);
        }

        GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(this.mc);
        AbstractMorph morph = this.hoverMorph;

        if (morph != null)
        {
            if (!(this.hoverCategory instanceof UserCategory))
            {
                Runnable runnable = this.parent.showGlobalMorphs(morph);

                if (runnable != null)
                {
                    contextMenu.action(Icons.UPLOAD, IKey.lang("morphcore.gui.creative.context.add_global"), runnable);
                }
            }

            contextMenu.action(Icons.EDIT, IKey.lang("morphcore.gui.creative.context.edit"), () -> this.parent.enterEditMorph(morph));
            contextMenu.action(Icons.COPY, IKey.lang("morphcore.gui.creative.context.copy_command"), () -> Minecraft.getInstance().keyboardHandler.setClipboard(getMorphCommand(morph)));
            contextMenu.action(Icons.COPY, IKey.lang("morphcore.gui.creative.context.copy"), () -> Minecraft.getInstance().keyboardHandler.setClipboard(morph.toNBT().toString()));
        }

        return contextMenu;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.drawMorphs(context);

        int h = this.getFullHeight();

        if (this.area.h != h)
        {
            this.flex().h(h);
            this.getParent().getParent().resize();
        }

        super.draw(context);
    }

    /**
     * Draw morphs
     */
    protected void drawMorphs(GuiContext context)
    {
        if (this.section.categories.isEmpty())
        {
            return;
        }

        /* Draw header */
        Icon toggleIcon = this.section.hidden ? Icons.MOVE_DOWN : Icons.MOVE_UP;

        context.graphics.fill(this.area.x, this.area.y, this.area.ex(), this.area.y + HEADER_HEIGHT, 0xbb000000);

        context.graphics.drawString(this.font, this.section.getTitle(), this.area.x + 7, this.area.y + 10 - this.font.lineHeight / 2, 0xffffff, true);
        toggleIcon.render(this.area.ex() - 18 - 3, this.area.y + 10 + (this.section.hidden ? 1 : -1), 0, 0.5F);

        /* Draw categories */
        int y = HEADER_HEIGHT;

        this.hoverMorph = null;
        this.hoverCategory = null;

        if (this.section.hidden)
        {
            return;
        }

        Area viewport = context.getViewport();
        int row = this.getPerRow();

        category:
        for (MorphCategory category : this.section.categories)
        {
            int count = this.getMorphsSize(category);

            GuiDraw.drawTextBackground(this.font, category.getTitle(), this.area.x + 7, this.area.y + y + 8 - this.font.lineHeight / 2, 0xeeeeee, ColorUtils.HALF_BLACK, 2);

            (category.hidden ? Icons.MOVE_DOWN : Icons.MOVE_UP).render(this.area.ex() - 18 - 3, this.area.y + y + CATEGORY_HEIGHT / 2 + (category.hidden ? 1 : -1), 0, 0.5F);

            Area.SHARED.copy(this.area);
            Area.SHARED.y = this.area.y + y;
            Area.SHARED.h = CATEGORY_HEIGHT + this.getCategoryHeight(category);

            if (Area.SHARED.isInside(context.mouseX, context.mouseY))
            {
                this.hoverCategory = category;
            }

            float x = 0;
            y += CATEGORY_HEIGHT + 5;

            if (category.isHidden() || (count == 0 && !this.noFilter()))
            {
                continue;
            }

            for (int i = 0, j = 0; i < category.getMorphs().size(); i ++)
            {
                AbstractMorph morph = category.getMorphs().get(i);

                if (!this.isMatching(morph))
                {
                    continue;
                }

                if (j != 0 && j % row == 0)
                {
                    x = 0;
                    y += this.cellHeight;
                }

                int mx = this.area.x + Math.round(x);
                int my = this.area.y + y;

                x += this.area.w / (float) row;
                int w = Math.round(x - (mx - this.area.x));

                Area.SHARED.set(mx, my, w, this.cellHeight);

                if (Area.SHARED.isInside(context.mouseX, context.mouseY))
                {
                    this.hoverMorph = morph;
                }

                if (Area.SHARED.intersects(viewport))
                {
                    GuiDraw.scissor(mx, my, w, this.cellHeight, context);
                    this.drawMorph(context, morph, mx, my, w, this.cellHeight, this.hoverMorph == morph, this.morph == morph);
                    GuiDraw.unscissor(context);
                }

                if (Area.SHARED.y > viewport.ey())
                {
                    break category;
                }

                j ++;
            }

            y += this.cellHeight + 5;
        }
    }

    /**
     * Draw individual morph
     */
    protected void drawMorph(GuiContext context, AbstractMorph morph, int x, int y, int w, int h, boolean hover, boolean selected)
    {
        if (selected && !morph.errorRendering)
        {
            context.graphics.fill(x, y, x + w, y + h, 0xaa000000 + MorphLib.primaryColor.get());
        }
        else if (hover)
        {
            context.graphics.fill(x, y, x + w, y + h, 0x66000000);
        }

        int spot = (int) (w * 0.4F);
        int spotX = x + w / 2;
        int spotY = y + h / 2;

        GuiDraw.drawDropCircleShadow(spotX, spotY, spot, (int) (spot * 0.65F), 10, 0x44000000, 0);

        if (morph.errorRendering)
        {
            GuiDraw.drawOutline(x, y, x + w, y + h, 0x88ff0000, 4);
            GuiDraw.drawOutline(x, y, x + w, y + h, 0xffff0000, 2);

            return;
        }

        if (!MorphUtils.renderOnScreen(morph, this.mc.player, x + w / 2, y + (int) (h * 0.7F), w * 0.4F, 1))
        {
            return;
        }

        if (selected)
        {
            GuiDraw.drawOutline(x, y, x + w, y + h, 0xff000000 + MorphLib.primaryColor.get(), 2);
        }

        if (morph.keybind != -1)
        {
            String key = Keys.getKeyName(morph.keybind);
            int kw = this.font.width(key);
            int kx = x + w - 6 - kw;
            int ky = y + h - 6 - this.font.lineHeight;

            context.graphics.fill(kx - 3, ky - 2, kx + kw + 3, ky + this.font.lineHeight + 2, 0xff000000);
            context.graphics.drawString(this.font, key, kx, ky, 0xffffff, true);
        }

        if (morph.favorite)
        {
            /* Stupid hack because the morph seems to change the blend function or something */
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GuiDraw.drawOutlinedIcon(Icons.FAVORITE, x + 2, y + 2, 0xffffffff);
        }
    }
}