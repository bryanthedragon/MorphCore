package bryanthedragon.morph.api.editors.panels;

import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.morph.client.gui.framework.elements.input.GuiTexturePicker;
import bryanthedragon.morph.client.gui.framework.elements.input.GuiTrackpadElement;
import bryanthedragon.morph.client.gui.utils.Elements;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.utils.Direction;
import bryanthedragon.morph.utils.resources.RLUtils;
import bryanthedragon.morph.api.morphs.main.EntityMorph;
import bryanthedragon.morph.client.gui.editor.GuiMorphPanel;
import bryanthedragon.morph.api.editors.GuiEntityMorph;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEntityPanel extends GuiMorphPanel<EntityMorph, GuiEntityMorph>
{
    public GuiButtonElement texture;
    public GuiTrackpadElement scale;
    public GuiTexturePicker picker;

    public GuiEntityPanel(Minecraft mc, GuiEntityMorph editor)
    {
        super(mc, editor);

        GuiElement element = new GuiElement(mc);

        element.flex().relative(this).y(1F).w(130).anchor(0, 1F).column(4).vertical().stretch().padding(10);

        this.scale = new GuiTrackpadElement(mc, (v) -> this.morph.scale = v.floatValue());
        this.texture = new GuiButtonElement(mc, IKey.lang("morphcore.gui.editor.texture"), (b) ->
        {
            this.picker.refresh();
            this.picker.fill(this.morph.userTexture);
            this.add(this.picker);
            this.picker.resize();
        });
        this.texture.tooltip(IKey.lang("morphcore.gui.editor.texture_tooltip"), Direction.TOP);
        this.picker = new GuiTexturePicker(mc, (rl) -> this.morph.userTexture = RLUtils.clone(rl));
        this.picker.flex().relative(this).wh(1F, 1F);

        element.add(this.texture);
        element.add(Elements.label(IKey.lang("morphcore.gui.editor.scale")).marginTop(12), this.scale);
        this.add(element);
    }

    @Override
    public void fillData(EntityMorph morph)
    {
        super.fillData(morph);

        this.scale.setValue(morph.scale);
    }
}