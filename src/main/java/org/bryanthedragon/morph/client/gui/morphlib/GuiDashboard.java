package org.bryanthedragon.morph.client.gui.morphlib;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.utils.Icons;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.config.gui.GuiConfigPanel;

import net.minecraft.client.Minecraft;

public class GuiDashboard extends GuiAbstractDashboard
{
    public static GuiDashboard dashboard;

    public GuiConfigPanel config;

    public static GuiDashboard get()
    {
        if (dashboard == null)
        {
            dashboard = new GuiDashboard(Minecraft.getInstance());
        }

        return dashboard;
    }

    public GuiDashboard(Minecraft mc)
    {
        super(mc);

        this.panels.registerPanel(new GuiGraphPanel(mc, this), IKey.lang("morphlib.gui.graph.tooltip"), Icons.GRAPH);
    }

    @Override
    protected GuiDashboardPanels createDashboardPanels(Minecraft mc)
    {
        return new GuiDashboardPanels(mc);
    }

    @Override
    protected void registerPanels(Minecraft mc)
    {
        this.panels.registerPanel(this.config = new GuiConfigPanel(mc, this), IKey.lang("morphlib.gui.config.tooltip"), Icons.GEAR);
        this.defaultPanel = this.config;

        if (MorphLib.debugPanel.get())
        {
            this.panels.registerPanel(new GuiDebugPanel(mc, this), IKey.str("Debug"), Icons.POSE);
        }

        this.panels.setPanel(this.config);
    }
}