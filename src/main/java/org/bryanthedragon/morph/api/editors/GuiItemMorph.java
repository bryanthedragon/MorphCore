package org.bryanthedragon.morph.api.editors;

import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.api.morphs.main.ItemMorph;
import org.bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import org.bryanthedragon.morph.utils.icons.IconUtils.MMIcons;
import org.bryanthedragon.morph.api.editors.panels.GuiItemPanel;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiItemMorph extends GuiAbstractMorph<ItemMorph>
{
    public GuiItemPanel item;

    public GuiItemMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.item = new GuiItemPanel(mc, this);
        this.registerPanel(this.item, IKey.lang("morphcore.gui.editor.item_morph"), MMIcons.ITEM);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof ItemMorph;
    }
}