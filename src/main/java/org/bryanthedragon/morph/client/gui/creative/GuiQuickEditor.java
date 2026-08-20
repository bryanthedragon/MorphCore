package bryanthedragon.morph.client.gui.creative;

import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.framework.elements.GuiScrollElement;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.morph.client.gui.framework.elements.list.GuiLabelSearchListElement;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.utils.Elements;
import bryanthedragon.morph.client.gui.utils.Label;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;

/**
 * Quick morph editor
 *
 * This GUI is responsible for providing quick access editing of the most used
 * fields in the morph editor
 */
public class GuiQuickEditor extends GuiElement
{
    public GuiCreativeMorphsList parent;

    public GuiButtonElement presetsButton;
    public GuiButtonElement randomPreset;
    public GuiButtonElement quickAccessButton;

    public GuiLabelSearchListElement<CompoundTag> presets;
    public GuiScrollElement quickAccess;

    private AbstractMorph last;

    public GuiQuickEditor(Minecraft mc, GuiCreativeMorphsList parent)
    {
        super(mc);

        this.parent = parent;

        this.presetsButton = new GuiButtonElement(mc, IKey.lang("morphcore.gui.creative.presets"), this::toggleVisibility);
        this.quickAccessButton = new GuiButtonElement(mc, IKey.lang("morphcore.gui.creative.quick"), this::toggleVisibility);

        this.presets = new GuiLabelSearchListElement<CompoundTag>(mc, this::setPreset);
        this.presets.flex().relative(this).set(10, 30, 0, 0).w(1F, -20).h(1F, -60);
        this.randomPreset = new GuiButtonElement(mc, IKey.lang("morphcore.gui.creative.random"), this::pickRandomPreset);
        this.randomPreset.flex().relative(this.presets).y(1F).w(1F);

        this.quickAccess = new GuiScrollElement(mc);
        this.quickAccess.setVisible(false);
        this.quickAccess.flex().relative(this).y(20).w(1F).h(1F, -20).column(5).vertical().stretch().scroll().padding(10).height(20);

        GuiElement row = Elements.row(mc, 0, this.presetsButton, this.quickAccessButton);
        row.flex().relative(this).w(1F).h(20);

        this.presets.add(this.randomPreset);
        this.add(row, this.presets, this.quickAccess);
    }

    private void toggleVisibility(GuiButtonElement button)
    {
        if (button == this.presetsButton)
        {
            this.presetsButton.setEnabled(false);
            this.quickAccessButton.setEnabled(true);

            this.presets.setVisible(true);
            this.quickAccess.setVisible(false);
        }
        else
        {
            this.presetsButton.setEnabled(true);
            this.quickAccessButton.setEnabled(false);

            this.presets.setVisible(false);
            this.quickAccess.setVisible(true);
        }
    }

    private void pickRandomPreset(GuiButtonElement button)
    {
        int i = (int) (Math.random() * this.presets.list.getList().size());

        this.presets.list.setIndex(i);
        this.setPreset(this.presets.list.getCurrent());
    }

    public void setMorph(AbstractMorph morph, GuiAbstractMorph<AbstractMorph> editor)
    {
        if (Objects.equals(this.last, morph))
        {
            return;
        }

        /* Fill quick access */
        this.quickAccess.removeAll();

        for (GuiElement element : editor.getFields(this.mc, this.parent, morph))
        {
            this.quickAccess.add(element);
        }

        /* Fill presets */
        List<Label<CompoundTag>> presets = editor.getPresets(morph);

        this.presets.list.clear();
        this.presets.list.add(presets);
        this.presets.list.sort();
        this.presets.filter("", true);

        this.toggleVisibility(this.presets.isVisible() ? this.presetsButton : this.quickAccessButton);
        this.resize();

        this.last = morph;
    }

    protected void setPreset(List<Label<CompoundTag>> label)
    {
        if (!label.isEmpty())
        {
            this.parent.getSelected().fromNBT(label.get(0).value);
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        this.area.draw(0xaa000000);

        if (this.presets.isVisible() && this.presets.list.getList().isEmpty())
        {
            context.graphics.drawCenteredString(this.font, "No factory presets found...", this.presets.area.mx(), this.presets.area.my() - this.font.lineHeight / 2, 0xffffff);
        }

        super.draw(context);
    }
}