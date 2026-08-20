package bryanthedragon.morph.client.gui.editor;

import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("rawtypes")
public class GuiMorphPanel<T extends AbstractMorph, E extends GuiAbstractMorph> extends GuiElement
{
    public E editor;
    public T morph;

    public GuiMorphPanel(Minecraft mc, E editor)
    {
        super(mc);

        this.editor = editor;
    }

    public void startEditing()
    {}

    public void finishEditing()
    {}

    public void fillData(T morph)
    {
        this.morph = morph;
    }

    public void fromNBT(CompoundTag tag)
    {}

    public CompoundTag toNBT()
    {
        return new CompoundTag();
    }
}