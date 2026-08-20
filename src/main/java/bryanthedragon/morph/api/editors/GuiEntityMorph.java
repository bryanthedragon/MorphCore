package bryanthedragon.morph.api.editors;

import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.client.gui.utils.Label;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;
import bryanthedragon.morph.bodypart.GuiBodyPartEditor;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.morph.api.editors.panels.GuiEntityPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiEntityMorph extends GuiAbstractMorph<EntityMorph>
{
    public static final List<String> animals = Arrays.asList("minecraft:pig", "minecraft:chicken", "minecraft:cow", "minecraft:mooshroom", "minecraft:polar_bear", "minecraft:sheep", "minecraft:ocelot");

    public GuiEntityPanel entityPanel;
    public GuiBodyPartEditor bodyPart;

    public GuiEntityMorph(Minecraft mc)
    {
        super(mc);

        this.bodyPart = new GuiBodyPartEditor(mc, this);
        this.entityPanel = new GuiEntityPanel(mc, this);

        this.registerPanel(this.bodyPart, IKey.lang("morphcore.gui.body_parts.parts"), Icons.LIMB);
        this.registerPanel(this.entityPanel, IKey.lang("morphcore.gui.editor.entity"), Icons.POSE);
        this.defaultPanel = this.entityPanel;
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof EntityMorph;
    }

    @Override
    public void startEdit(EntityMorph morph)
    {
        if (morph.getEntity() == null)
        {
            morph.setupEntity(this.mc.world);
        }

        morph.parts.reinitBodyParts();
        morph.setupLimbs();
        this.bodyPart.setLimbs(morph.limbs.keySet());

        super.startEdit(morph);
    }

    @Override
    public List<Label<CompoundTag>> getPresets(EntityMorph morph)
    {
        List<Label<CompoundTag>> presets = new ArrayList<Label<CompoundTag>>();
        String name = morph.name;

        if (animals.contains(name))
        {
            this.addPreset(morph, presets, "Baby", "{Age:-1}");
        }

        if (name.equals("minecraft:sheep"))
        {
            this.addPreset(morph, presets, "Sheared", "{Sheared:1b}");
            this.addPreset(morph, presets, "Sheared (baby)", "{Age:-1,Sheared:1b}");

            for (int i = 1; i < 16; i++)
            {
                this.addPreset(morph, presets, "Colored sheep #" + i, "{Color:" + i + "}");
            }

            this.addPreset(morph, presets, "Jeb", "{CustomName:\"jeb_\"}");
            this.addPreset(morph, presets, "Baby Jeb", "{Age:-1,CustomName:\"jeb_\"}");
        }

        if (name.equals("minecraft:slime") || name.equals("minecraft:magma_cube"))
        {
            this.addPreset(morph, presets, "Medium", "{Size:1}");
            this.addPreset(morph, presets, "Big", "{Size:2}");
        }

        if (name.equals("minecraft:ocelot"))
        {
            for (int i = 1; i < 4; i++)
            {
                this.addPreset(morph, presets, "Cat #" + i, "{CatType:" + i + "}");
                this.addPreset(morph, presets, "Cat #" + i + " (baby)", "{CatType:" + i + ",Age:-1}");
            }
        }

        if (name.equals("minecraft:parrot"))
        {
            for (int i = 1; i <= 4; i++)
            {
                this.addPreset(morph, presets, "Parrot #" + i, "{Variant:" + i + "}");
            }
        }

        if (name.equals("minecraft:horse"))
        {
            for (int i = 1; i <= 6; i++)
            {
                this.addPreset(morph, presets, "Horse #" + i, "{Variant:" + i + "}");
            }
        }

        if (name.equals("minecraft:llama"))
        {
            for (int i = 1; i < 4; i++)
            {
                this.addPreset(morph, presets, "Llama #" + i, "{Variant:" + i + "}");
            }
        }

        if (name.equals("minecraft:bat"))
        {
            this.addPreset(morph, presets, "Flying", "{BatFlags:2}");
        }

        if (name.equals("minecraft:rabbit"))
        {
            for (int i = 1; i < 6; i++)
            {
                this.addPreset(morph, presets, "Rabbit #" + i, "{RabbitType:" + i + "}");
            }

            this.addPreset(morph, presets, "Toast", "{CustomName:\"Toast\"}");
        }

        if (name.equals("minecraft:zombie"))
        {
            this.addPreset(morph, presets, "Baby", "{IsBaby:1b}");
        }

        if (name.equals("minecraft:villager") || name.equals("minecraft:zombie_villager"))
        {
            this.addPreset(morph, presets, "Librarian", "{ProfessionName:\"minecraft:librarian\"}");
            this.addPreset(morph, presets, "Priest", "{ProfessionName:\"minecraft:priest\"}");
            this.addPreset(morph, presets, "Smith", "{ProfessionName:\"minecraft:smith\"}");
            this.addPreset(morph, presets, "Butcher", "{ProfessionName:\"minecraft:butcher\"}");
            this.addPreset(morph, presets, "Nitwit", "{ProfessionName:\"minecraft:nitwit\"}");
        }

        return presets;
    }

    @Override
    protected void addPreset(AbstractMorph morph, List<Label<CompoundTag>> list, String label, String json)
    {
        try
        {
            CompoundTag tag = morph.toNBT();
            CompoundTag entity = new CompoundTag();

            tag.removeTag("EntityData");
            entity.setTag("EntityData", JsonToNBT.getTagFromJson(json));
            tag.merge(entity);
            list.add(new Label<CompoundTag>(IKey.str(label), tag));
        }
        catch (Exception e)
        {}
    }
}