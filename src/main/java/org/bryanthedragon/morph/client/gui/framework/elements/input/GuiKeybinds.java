package org.bryanthedragon.morph.client.gui.framework.elements.input;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.framework.elements.GuiScrollElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.client.gui.utils.Keybind;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiKeybinds extends GuiScrollElement
{
    public Map<String, KeybindCategory> keybinds = new HashMap<String, KeybindCategory>();

    public GuiKeybinds(Minecraft mc)
    {
        super(mc);

        this.hideTooltip();
        this.scroll.opposite = true;
    }

    public void addKeybind(Keybind keybind)
    {
        KeybindCategory category = this.keybinds.get(keybind.category.get());

        if (category == null)
        {
            category = new KeybindCategory(keybind.category);
            this.keybinds.put(keybind.category.get(), category);
        }

        category.add(keybind);
    }

    @Override
    public void draw(GuiContext context)
    {
        int cx = 40;
        context.graphics.fill(this.area.x, this.area.y, cx, this.area.ey(), 0xdd000000);
        GuiDraw.drawHorizontalGradientRect(cx, this.area.y, this.area.ex(), this.area.ey(), 0xdd000000, 0);

        super.draw(context);
    }

    @Override
    protected void preDraw(GuiContext context)
    {
        super.preDraw(context);

        int x = this.area.x + 10;
        int y = this.area.y + 10;
        int i = 0;

        KeybindCategory general = this.keybinds.get("");

        i = general.draw(context, x, y, i) + 10;

        for (KeybindCategory category : this.keybinds.values())
        {
            if (category != general)
            {
                i = category.draw(context, x, y, i) + 10;
            }
        }

        this.keybinds.clear();
        this.scroll.scrollSize = i + 3;
        this.scroll.clamp();
    }

    public static class KeybindCategory
    {
        public IKey title;
        public List<Keybind> keybinds = new ArrayList<Keybind>();
        public boolean shouldClean;

        public KeybindCategory(IKey title)
        {
            this.title = title;
        }

        public void add(Keybind keybind)
        {
            if (this.shouldClean)
            {
                this.keybinds.clear();
                this.shouldClean = false;
            }

            this.keybinds.add(keybind);
        }

        public int draw(GuiContext context, int x, int y, int i)
        {
            int color = 0xff000000 + MorphLib.primaryColor.get();

            String title = this.title.get();

            if (!title.isEmpty())
            {
                context.graphics.fill(x - 10, y + i - 2, x + context.font.width(title) + 2, y + i + context.font.lineHeight + 2, color);
                context.graphics.drawString(context.font, title, x, y + i, 0xffffff);
                i += 14;
            }

            for (Keybind keybind : this.keybinds)
            {
                String combo = keybind.getKeyCombo();
                int w = context.font.width(combo);

                context.graphics.fill(x - 2, y + i - 2, x + w + 2, y + i + context.font.lineHeight + 2, color);
                context.graphics.drawString(context.font, combo, x, y + i, 0xffffff);
                context.graphics.drawString(context.font, keybind.label.get(), x + w + 5, y + i, 0xffffff, true);
                i += 14;
            }

            return i;
        }
    }
}