package bryanthedragon.morph.client.gui.framework.elements.modals;

import org.lwjgl.glfw.GLFW;

import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;


public class GuiMessageModal extends GuiModal
{
    public GuiButtonElement button;

    public GuiMessageModal(Minecraft mc, IKey label)
    {
        super(mc, label);

        this.button = new GuiButtonElement(mc, IKey.lang("morphlib.gui.ok"), (b) -> this.removeFromParent());

        this.bar.add(this.button);
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (context.keyCode == GLFW.GLFW_KEY_ENTER || context.keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            this.button.clickItself(context);

            return true;
        }

        return false;
    }
}