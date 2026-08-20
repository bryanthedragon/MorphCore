package bryanthedragon.morph.client.gui.creative;

import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

public class GuiNestedEdit extends GuiElement
{
    public GuiButtonElement pick;
    public GuiButtonElement edit;

    public GuiNestedEdit(Minecraft mc, Consumer<Boolean> callback)
    {
        this(mc, true, callback);
    }

    public GuiNestedEdit(Minecraft mc, boolean keybinds, Consumer<Boolean> callback)
    {
        super(mc);

        this.edit = new GuiButtonElement(mc, IKey.lang("morphcore.gui.creative.edit"), (b) -> callback.accept(true));
        this.pick = new GuiButtonElement(mc, IKey.lang("morphcore.gui.creative.pick"), (b) -> callback.accept(false));

        this.edit.flex().relative(this).h(1F);
        this.pick.flex().relative(this).h(1F);

        this.flex().h(20).row(0);
        this.add(this.pick, this.edit);

        if (keybinds)
        {
            this.keys().register(this.pick.label, GLFW.GLFW_KEY_P, () -> this.pick.clickItself(GuiBase.getCurrent()));
            this.keys().register(this.edit.label, GLFW.GLFW_KEY_E, () -> this.edit.clickItself(GuiBase.getCurrent()));
        }
    }

    public void setMorph(AbstractMorph morph)
    {
        this.edit.setEnabled(morph != null);
    }
}