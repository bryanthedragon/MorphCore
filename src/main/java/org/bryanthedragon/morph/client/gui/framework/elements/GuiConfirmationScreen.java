package org.bryanthedragon.morph.client.gui.framework.elements;

import org.bryanthedragon.morph.client.gui.framework.GuiBase;
import org.bryanthedragon.morph.client.gui.framework.elements.modals.GuiConfirmModal;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

public class GuiConfirmationScreen extends GuiBase
{
    private Consumer<Boolean> callback;
    private boolean value;

    public GuiConfirmationScreen(IKey label, Consumer<Boolean> callback)
    {
        super();

        this.callback = callback;

        this.root.add(GuiConfirmModal.createTemplate(Minecraft.getInstance(), this.viewport, label, (value) ->
        {
            this.value = value;
            closeScreen();
        }));
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    protected void closeScreen()
    {
        this.callback.accept(this.value);
        super.closeScreen();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
