package bryanthedragon.vanilla_pack.editors;

import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.client.gui.utils.Label;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.BlockMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.vanilla_pack.editors.panels.GuiItemStackPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiBlockMorph extends GuiAbstractMorph<BlockMorph>
{
    public GuiItemStackPanel block;

    public GuiBlockMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.block = new GuiItemStackPanel(mc, this);
        this.registerPanel(this.block, IKey.lang("morphcore.gui.panels.block"), Icons.BLOCK);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof BlockMorph;
    }

    @Override
    public List<Label<CompoundTag>> getPresets(BlockMorph morph)
    {
        List<Label<CompoundTag>> presets = new ArrayList<Label<CompoundTag>>();

        this.addPreset(morph, presets, "Stone", "{Block:\"minecraft:stone\"}");
        this.addPreset(morph, presets, "Cobblestone", "{Block:\"minecraft:cobblestone\"}");
        this.addPreset(morph, presets, "Grass", "{Block:\"minecraft:grass\"}");
        this.addPreset(morph, presets, "Dirt", "{Block:\"minecraft:dirt\"}");
        this.addPreset(morph, presets, "Log", "{Block:\"minecraft:log\"}");
        this.addPreset(morph, presets, "Diamond block", "{Block:\"minecraft:diamond_block\"}");
        this.addPreset(morph, presets, "Sponge", "{Block:\"minecraft:sponge\"}");
        this.addPreset(morph, presets, "Deadbush", "{Block:\"minecraft:deadbush\"}");

        return presets;
    }
}