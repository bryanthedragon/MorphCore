package org.bryanthedragon.morph.events;

import org.bryanthedragon.morph.client.gui.morphlib.GuiAbstractDashboard;

import net.minecraftforge.eventbus.api.Event;

public class RegisterDashboardPanels extends Event
{
    public final GuiAbstractDashboard dashboard;

    public RegisterDashboardPanels(GuiAbstractDashboard dashboard)
    {
        this.dashboard = dashboard;
    }
}