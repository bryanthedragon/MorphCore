package bryanthedragon.vanilla_pack.editors;

import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.ItemMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.morph.util.MMIcons;
import bryanthedragon.vanilla_pack.editors.panels.GuiItemPanel;
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