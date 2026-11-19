package org.bryanthedragon.morph.client.gui.morphlib;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.framework.GuiBase;
import org.bryanthedragon.morph.client.gui.framework.elements.buttons.GuiIconElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.client.gui.utils.Keybind;
import org.bryanthedragon.morph.events.RegisterDashboardPanels;
import org.bryanthedragon.morph.utils.permissions.PermissionUtils;
import org.bryanthedragon.morph.utils.helpers.op.OpHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

public abstract class GuiAbstractDashboard extends GuiBase
{
    public GuiDashboardPanels panels;

    @SuppressWarnings("rawtypes")
    public GuiDashboardPanel defaultPanel;

    private boolean wasClosed = true;

    @SuppressWarnings("unused")
    private int opLevel = -1;

    public GuiAbstractDashboard(Minecraft mc)
    {
        this.panels = this.createDashboardPanels(mc);

        this.panels.flex().relative(this.viewport).wh(1F, 1F);
        this.registerPanels(mc);

        MorphLib.EVENT_BUS.post(new RegisterDashboardPanels(this));

        this.root.add(this.panels);
    }

    protected abstract GuiDashboardPanels createDashboardPanels(Minecraft mc);

    protected abstract void registerPanels(Minecraft mc);

    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void removed()
    {
        this.onClose();
        super.removed();
        
        this.panels.close();
        this.wasClosed = true;
    }

    public void init()
    {
        this.checkPermissions();

        if (this.wasClosed)
        {
            this.wasClosed = false;
            this.panels.open();
            this.panels.setPanel(this.panels.view.delegate);
        }

        super.init();
    }

    @SuppressWarnings("rawtypes")
    private void checkPermissions()
    {
        int newOpLevel = OpHelper.getPlayerOpLevel();

        for (GuiDashboardPanel panel : this.panels.panels)
        {
            GuiIconElement button = this.panels.getButton(panel);

            Consumer<Boolean> task = (enabled) ->
            {
                button.setEnabled(enabled);

                for (Keybind keybind : this.panels.keys().keybinds)
                {
                    keybind.active(enabled);
                }
            };

            if (panel.getRequiredPermission() != null)
            {
                PermissionUtils.hasPermission(Minecraft.getInstance().player, panel.getRequiredPermission(), task);
            }
            else
            {
                task.accept(true);
            }
        }

        GuiDashboardPanel current = this.panels.view.delegate;

        if (current != null && current.getRequiredPermission() != null)
        {
            this.panels.setPanel(null);

            PermissionUtils.hasPermission(Minecraft.getInstance().player, current.getRequiredPermission(), (allowed) ->
            {
                if (allowed)
                {
                    this.panels.setPanel(current);
                }
                else
                {
                    this.panels.setPanel(this.defaultPanel);
                }
            });
        }
        else if (current == null)
        {
            this.panels.setPanel(null);
        }

        this.opLevel = newOpLevel;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if (this.panels.view.delegate != null && this.panels.view.delegate.needsBackground())
        {
            GuiDraw.drawCustomBackground(0, 0, this.width, this.height);
        }
        else
        {
            graphics.fillGradient(0, 0, this.width, this.height / 8, 0x44000000, 0);
            graphics.fillGradient(0, this.height - this.height / 8, this.width, this.height, 0, 0x44000000);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}