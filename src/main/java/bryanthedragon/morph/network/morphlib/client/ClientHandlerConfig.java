package bryanthedragon.morph.network.morphlib.client;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.client.gui.morphlib.GuiDashboard;
import bryanthedragon.morph.config.Config;
import bryanthedragon.morph.config.gui.GuiConfigPanel;
import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.network.morphlib.common.PacketConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.screens.Screen;
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
