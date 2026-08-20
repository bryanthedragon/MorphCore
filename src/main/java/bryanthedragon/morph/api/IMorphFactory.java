package bryanthedragon.morph.api;

import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Morph factory
 * 
 * This factory interface will be responsible for providing different resources 
 * related to morphs like custom models, abilities, attacks or actions.
 */
public interface IMorphFactory
{
    /**
     * Register here everything that is required by morph manager system
     */
    public void register(MorphManager manager);

    /**
     * Register morph editors which will be available in the creative 
     * morphs for editing 
     */

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("rawtypes")
    public void registerMorphEditors(Minecraft mc, List<GuiAbstractMorph> editors);

    /**
     * Does this factory has morph by given name? 
     */
    public boolean hasMorph(String name);

    /**
     * Get a morph from NBT
     */
    public AbstractMorph getMorphFromNBT(CompoundTag tag);
}