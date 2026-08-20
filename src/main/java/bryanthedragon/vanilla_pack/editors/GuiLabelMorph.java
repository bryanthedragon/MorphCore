package bryanthedragon.vanilla_pack.editors;

import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.morph.util.MMIcons;
import bryanthedragon.vanilla_pack.editors.panels.GuiLabelPanel;
import bryanthedragon.vanilla_pack.morphs.LabelMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

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