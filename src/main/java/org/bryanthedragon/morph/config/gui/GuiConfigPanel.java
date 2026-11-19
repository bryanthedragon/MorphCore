package org.bryanthedragon.morph.config.gui;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import org.bryanthedragon.morph.client.gui.framework.elements.GuiScrollElement;
import org.bryanthedragon.morph.client.gui.framework.elements.buttons.GuiIconElement;
import org.bryanthedragon.morph.client.gui.framework.elements.list.GuiLabelListElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiLabel;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.text.ITextColoring;
import org.bryanthedragon.morph.client.gui.morphlib.GuiAbstractDashboard;
import org.bryanthedragon.morph.client.gui.morphlib.GuiDashboardPanel;
import org.bryanthedragon.morph.client.gui.utils.Elements;
import org.bryanthedragon.morph.client.gui.utils.Icons;
import org.bryanthedragon.morph.client.gui.utils.Label;
import org.bryanthedragon.morph.client.gui.utils.ScrollDirection;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.config.Config;
import org.bryanthedragon.morph.config.values.IConfigGuiProvider;
import org.bryanthedragon.morph.config.values.Value;
import org.bryanthedragon.morph.network.morphlib.Dispatcher;
import org.bryanthedragon.morph.network.morphlib.common.PacketRequestConfigs;
import org.bryanthedragon.morph.utils.vector.Direction;
import org.bryanthedragon.morph.utils.helpers.op.OpHelper;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GuiConfigPanel extends GuiDashboardPanel<GuiAbstractDashboard>
{
    public GuiIconElement request;
    public GuiIconElement reload;
    public GuiLabelListElement<String> mods;
    public GuiScrollElement options;

    private Config config;
    private IKey title = IKey.lang("morphlib.gui.config.title");

    private Map<String, Config> serverConfigs;

    public GuiConfigPanel(Minecraft mc, GuiAbstractDashboard dashboard)
    {
        super(mc, dashboard);

        this.request = new GuiIconElement(mc, Icons.DOWNLOAD, (button) -> this.request());
        this.request.tooltip(IKey.lang("morphlib.gui.config.request_tooltip"), Direction.BOTTOM);
        this.reload = new GuiIconElement(mc, Icons.REFRESH, (button) -> this.reload());
        this.reload.tooltip(IKey.lang("morphlib.gui.config.reload_tooltip"), Direction.BOTTOM);
        this.mods = new GuiLabelListElement<String>(mc, (mod) -> this.selectConfig(mod.get(0).value));
        this.options = new GuiScrollElement(mc, ScrollDirection.HORIZONTAL);
        this.options.scroll.scrollSpeed = 51;

        this.reload.flex().relative(this).set(120 - 14, 12, 16, 16);
        this.request.flex().relative(this.reload.resizer()).set(-20, 0, 16, 16);
        this.mods.flex().relative(this).set(10, 35, 110, 0).h(1, -45);
        this.options.flex().relative(this).set(130, 0, 0, 0).w(1, -130).h(1F);
        this.options.flex().column(5).scroll().width(240).height(20).padding(15);

        this.fillClientMods();

        this.add(this.reload, this.request, this.mods, this.options);
        this.selectConfig("morphlib");
        this.markContainer();
    }

    private void fillClientMods()
    {
        for (Config config : MorphLib.proxy.configs.modules.values())
        {
            if (!config.isServerSide())
            {
                this.mods.add(IKey.lang(config.getTitleKey()), config.id);
            }
        }

        this.mods.sort();
    }

    @Override
    public void open()
    {
        this.request.setVisible(!Minecraft.getInstance().hasSingleplayerServer() && OpHelper.isPlayerOp());
    }

    @Override
    public void close()
    {
        if (this.serverConfigs != null)
        {
            this.request();
        }
    }

    @Override
    public boolean canBeOpened(int opLevel)
    {
        return true;
    }

    public void storeServerConfig(Config config)
    {
        this.serverConfigs.put(config.id, config);
        this.mods.add(IKey.lang(config.getTitleKey()), config.id);
        this.mods.sort();

        if (config.id.equals("morphlib"))
        {
            this.selectConfig("morphlib");
        }
    }

    private void request()
    {
        this.config = null;
        this.mods.clear();

        if (this.serverConfigs == null)
        {
            this.serverConfigs = new HashMap<String, Config>();
            this.mods.setCurrent((Label<String>) null);

            Dispatcher.sendToServer(new PacketRequestConfigs());
        }
        else
        {
            this.serverConfigs = null;

            this.fillClientMods();
            this.selectConfig("morphlib");
        }

        this.reload.setEnabled(this.serverConfigs == null);
        this.request.both(this.serverConfigs == null ? Icons.DOWNLOAD : Icons.UPLOAD);
    }

    private void reload()
    {
        if (this.serverConfigs == null)
        {
            MorphLib.proxy.configs.reload();
            this.refresh();
        }
    }

    private void selectConfig(String mod)
    {
        this.mods.setCurrentValue(mod);
        this.config = this.serverConfigs == null ? MorphLib.proxy.configs.modules.get(mod) : this.serverConfigs.get(mod);
        this.refresh();
    }

    public void refresh()
    {
        if (this.config == null)
        {
            return;
        }

        this.options.removeAll();

        boolean first = true;
        boolean checkForClient = this.serverConfigs != null;
        boolean isSingleplayer = Minecraft.getInstance().hasSingleplayerServer();

        for (Value category : this.config.values.values())
        {
            if (!category.isVisible() || (checkForClient && category.isClientSide()))
            {
                continue;
            }

            String catTitleKey = this.config.getCategoryTitleKey(category);
            String catTooltipKey = this.config.getCategoryTooltipKey(category);

            GuiLabel label = Elements.label(IKey.lang(catTitleKey)).anchor(0, 1).background();

            if (!first)
            {
                label.margin.top(24);
            }

            label.tooltip(IKey.lang(catTooltipKey), Direction.BOTTOM).flex().w(this.font.width(label.label.get()));
            this.options.add(label);

            for (Value value : category.getSubValues())
            {
                if (!value.isVisible() || (checkForClient && value.isClientSide()))
                {
                    continue;
                }

                if (!(value instanceof IConfigGuiProvider))
                {
                    continue;
                }

                for (GuiElement element : ((IConfigGuiProvider) value).getFields(this.mc, this))
                {
                    this.options.add(element);

                    /* Distinguish client side options from server side */
                    if (!isSingleplayer && !checkForClient && !value.isClientSide())
                    {
                        for (ITextColoring elem : element.getChildren(ITextColoring.class, new ArrayList<ITextColoring>(), true))
                        {
                            elem.setColor(0x999999, true);
                        }
                    }
                }
            }

            first = false;
        }

        this.resize();
    }

    @Override
    public void draw(GuiContext context)
    {
        this.mods.area.draw(0xdd000000, -10, -35, -10, -10);
        context.graphics.drawString(this.font, this.title.get(), this.area.x + 10, this.area.y + 20 - this.font.lineHeight / 2, 0xffffff, true);

        super.draw(context);
    }
}