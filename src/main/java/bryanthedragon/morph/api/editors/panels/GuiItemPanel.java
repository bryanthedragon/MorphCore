package bryanthedragon.morph.api.editors.panels;

import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiCirculateElement;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiSlotElement;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiToggleElement;
import bryanthedragon.morph.client.gui.framework.elements.input.GuiTexturePicker;
import bryanthedragon.morph.client.gui.utils.Elements;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.utils.resources.RLUtils;
import bryanthedragon.morph.api.morphs.main.ItemMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.morph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiItemPanel extends GuiMorphPanel<ItemMorph, GuiAbstractMorph<? extends ItemMorph>>
{
    public GuiSlotElement slot;

    public GuiToggleElement lighting;
    public GuiCirculateElement transform;
    public GuiButtonElement texture;
    public GuiToggleElement animation;
    public GuiToggleElement itemFromEquipment;
    public GuiCirculateElement equipmentSlot;

    public GuiTexturePicker picker;

    public GuiItemPanel(Minecraft mc, GuiAbstractMorph<? extends ItemMorph> editor)
    {
        super(mc, editor);

        this.slot = new GuiSlotElement(mc, 0, (stack) -> this.morph.setStack(stack));
        this.lighting = new GuiToggleElement(mc, IKey.lang("morphcore.gui.label.lighting"), (b) -> this.morph.lighting = b.isToggled());
        this.transform = new GuiCirculateElement(mc, (b) ->
        {
            ItemCameraTransforms.TransformType type = ItemCameraTransforms.TransformType.values()[b.getValue()];

            this.morph.transform = ItemMorph.getTransformTypes().inverse().get(type);
        });
        this.animation = new GuiToggleElement(mc, IKey.lang("morphcore.gui.item.animation"), (b) -> this.morph.animation = b.isToggled());
        this.itemFromEquipment = new GuiToggleElement(mc, IKey.lang("morphcore.gui.item.item_from_equipment"), (b) -> this.morph.itemFromEquipment = b.isToggled());
        this.equipmentSlot = new GuiCirculateElement(mc, (b) -> this.morph.equipmentSlot = EquipmentSlot.values()[b.getValue()]);

        this.slot.flex().relative(this).x(0.5F, 0).y(1, -10).wh(32, 32).anchor(0.5F, 1);

        for (ItemCameraTransforms.TransformType transform : ItemCameraTransforms.TransformType.values())
        {
            String key = ItemMorph.getTransformTypes().inverse().get(transform);

            this.transform.addLabel(IKey.lang("morphcore.gui.item.transform." + key));
        }

        for (EquipmentSlot slot : EquipmentSlot.values())
        {
            this.equipmentSlot.addLabel(IKey.lang("morphcore.gui.item.equipment_slot." + slot.getName()));
        }

        this.texture = new GuiButtonElement(mc, IKey.lang("morphcore.gui.editor.texture"), (b) ->
        {
            this.picker.refresh();
            this.picker.fill(this.morph.texture);
            this.add(this.picker);
            this.picker.resize();
        });
        this.picker = new GuiTexturePicker(mc, (rl) -> this.morph.texture = RLUtils.clone(rl));
        this.picker.flex().relative(this).wh(1F, 1F);

        GuiElement column = Elements.column(mc, 5, this.lighting, this.transform, this.texture, this.animation, this.itemFromEquipment.marginTop(12), this.equipmentSlot);

        column.flex().relative(this).xy(10, 10).w(110);

        this.add(this.slot, column);
    }

    @Override
    public void fillData(ItemMorph morph)
    {
        super.fillData(morph);

        ItemCameraTransforms.TransformType type = morph.getTransformType();

        this.slot.setStack(morph.getStack());
        this.lighting.toggled(morph.lighting);
        this.transform.setValue(type.ordinal());
        this.animation.toggled(morph.animation);
        this.itemFromEquipment.toggled(morph.itemFromEquipment);
        this.equipmentSlot.setValue(morph.equipmentSlot.ordinal());
    }
}