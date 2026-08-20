package bryanthedragon.morph.client.gui.morphlib;

import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.GuiPanelBase;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiIconElement;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.utils.Icon;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.permissions.utils.PermissionUtils;
import bryanthedragon.morph.utils.vector.Direction;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class GuiDashboardPanels extends GuiPanelBase<GuiDashboardPanel>
{
    public GuiDashboardPanels(Minecraft mc)
    {
        super(mc, Direction.LEFT);
    }

    public void open()
    {
        for (GuiDashboardPanel panel : this.panels)
        {
            Consumer<Boolean> task = (enabled) ->
            {
                if (enabled)
                {
                    panel.open();
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
    }

    public void close()
    {
        for (GuiDashboardPanel panel : this.panels)
        {
            panel.close();
        }
    }

    @Override
    public void setPanel(GuiDashboardPanel panel)
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.disappear();
        }

        super.setPanel(panel);

        if (this.view.delegate != null)
        {
            this.view.delegate.appear();
        }
    }

    @Override
    public GuiIconElement registerPanel(GuiDashboardPanel panel, IKey tooltip, Icon icon)
    {
        GuiIconElement element = super.registerPanel(panel, tooltip, icon);

        int key = this.getKeybind();

        if (key != -1)
        {
            element.keys().register(IKey.comp(IKey.lang("morphlib.gui.dashboard.open_panel"), tooltip), key, () -> element.clickItself(GuiBase.getCurrent())).category(IKey.lang("morphlib.gui.dashboard.category"));
        }

        return element;
    }

    protected int getKeybind()
    {
        int size = this.panels.size();

        switch (size)
        {
            case 1: return GLFW.GLFW_KEY_KP_0;
            case 2: return GLFW.GLFW_KEY_KP_1;
            case 3: return GLFW.GLFW_KEY_KP_2;
            case 4: return GLFW.GLFW_KEY_KP_3;
            case 5: return GLFW.GLFW_KEY_KP_4;
            case 6: return GLFW.GLFW_KEY_KP_5;
            case 7: return GLFW.GLFW_KEY_KP_6;
            case 8: return GLFW.GLFW_KEY_KP_7;
            case 9: return GLFW.GLFW_KEY_KP_8;
            case 10: return GLFW.GLFW_KEY_KP_9;
        }

        return -1;
    }

    @Override
    protected void drawBackground(GuiContext context, int x, int y, int w, int h)
    {
        context.graphics.fill(x, y, x + w, y + h, 0xff111111);
    }
}