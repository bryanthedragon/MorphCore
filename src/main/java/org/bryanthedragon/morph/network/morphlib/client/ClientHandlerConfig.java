package org.bryanthedragon.morph.network.morphlib.client;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.morphlib.GuiDashboard;
import org.bryanthedragon.morph.config.Config;
import org.bryanthedragon.morph.config.gui.GuiConfigPanel;
import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.network.morphlib.common.PacketConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientHandlerConfig extends ClientMessageHandler<PacketConfig>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer player, PacketConfig message)
    {
        if (message.overwrite)
        {
            Config present = MorphLib.proxy.configs.modules.get(message.config.id);

            present.copyServer(message.config);
        }
        else
        {
            Screen screen = Minecraft.getInstance().screen;

            if (screen instanceof GuiDashboard)
            {
                GuiConfigPanel panel = ((GuiDashboard) screen).config;

                panel.storeServerConfig(message.config);
            }
        }
    }
}
