package bryanthedragon.morph.api.editors;

import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.LabelMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.morph.utils.icons.IconUtils.MMIcons;
import bryanthedragon.morph.api.editors.panels.GuiLabelPanel;

import net.minecraft.client.Minecraft;


public class GuiLabelMorph extends GuiAbstractMorph<LabelMorph>
{
    public GuiLabelPanel label;

    public GuiLabelMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.label = new GuiLabelPanel(mc, this);
        this.registerPanel(this.label, IKey.lang("morphcore.gui.label.label"), MMIcons.LABEL);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof LabelMorph;
    }
}