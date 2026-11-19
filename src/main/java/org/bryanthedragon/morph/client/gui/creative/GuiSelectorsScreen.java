package org.bryanthedragon.morph.client.gui.creative;

import org.bryanthedragon.morph.client.gui.framework.GuiBase;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.creative.sections.MorphSection;
import org.bryanthedragon.morph.api.creative.sections.UserSection;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.capabilities.render.EntitySelector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

public class GuiSelectorsScreen extends GuiBase
{
    public GuiSelectorEditor editor;
    public GuiCreativeMorphsMenu menu;

    public GuiSelectorsScreen(Minecraft mc)
    {
        this.editor = new GuiSelectorEditor(mc, true);
        this.menu = new GuiCreativeMorphsMenu(mc, true, this::setMorph);
        this.menu.setVisible(true);

        this.editor.flex().relative(this.viewport).wTo(this.menu.flex()).h(1F);
        this.menu.flex().relative(this.viewport).x(140).h(1F).wTo(this.root.flex(), 1F);

        this.root.add(this.menu, this.editor);
    }

    private void setMorph(AbstractMorph morph)
    {
        EntitySelector selector = this.editor.getSelector();
        CompoundTag tag =null;

        if (selector != null)
        {
            tag = selector.morph;
        }

        this.editor.setMorph(morph);

        if (tag != null)
        {
            AbstractMorph oldMorph = MorphManager.INSTANCE.morphFromNBT(tag);

            if (oldMorph != null)
            {
                this.addToRecent(oldMorph);
            }
        }
    }

    private void addToRecent(AbstractMorph oldMorph)
    {
        for (MorphSection section : MorphManager.INSTANCE.list.sections)
        {
            if (section instanceof UserSection)
            {
                UserSection user = (UserSection) section;

                if (user.recent.getEqual(oldMorph) == null)
                {
                    user.recent.add(oldMorph);

                    return;
                }
            }
        }
    }

    public boolean doesGuiPauseGame()
    {
        return MorphCore.pauseGUIInSP.get();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        GuiDraw.drawCustomBackground(0, 0, this.width, this.height);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}