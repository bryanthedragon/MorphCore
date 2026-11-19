package org.bryanthedragon.morph.api.editors.panels;

import org.bryanthedragon.morph.client.gui.framework.elements.buttons.GuiSlotElement;
import org.bryanthedragon.morph.client.gui.framework.elements.buttons.GuiToggleElement;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.api.morphs.main.ItemStackMorph;
import org.bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import org.bryanthedragon.morph.client.gui.editor.GuiMorphPanel;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiItemStackPanel extends GuiMorphPanel<ItemStackMorph, GuiAbstractMorph<? extends ItemStackMorph>>
{
    public GuiSlotElement slot;
    public GuiToggleElement lighting;

    public GuiItemStackPanel(Minecraft mc, GuiAbstractMorph<? extends ItemStackMorph> editor)
    {
        super(mc, editor);

        this.slot = new GuiSlotElement(mc, 0, (stack) -> this.morph.setStack(stack));
        this.lighting = new GuiToggleElement(mc, IKey.lang("morphcore.gui.label.lighting"), (b) -> this.morph.lighting = b.isToggled());

        this.slot.flex().relative(this).x(0.5F, 0).y(1, -10).wh(32, 32).anchor(0.5F, 1);
        this.lighting.flex().relative(this).xy(10, 10).w(110);

        this.add(this.slot, this.lighting);
    }

    @Override
    public void fillData(ItemStackMorph morph)
    {
        super.fillData(morph);

        this.slot.setStack(morph.getStack());
        this.lighting.toggled(morph.lighting);
    }
}