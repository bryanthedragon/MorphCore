package bryanthedragon.morph.network.morphlib.client;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.client.gui.morphlib.GuiDashboard;
import bryanthedragon.morph.config.Config;
import bryanthedragon.morph.config.gui.GuiConfigPanel;
import bryanthedragon.morph.network.ClientMessageHandler;
import bryanthedragon.morph.network.morphlib.common.PacketConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.PlayerSP;
import net.minecraft.client.gui.Screen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerConfig extends ClientMessageHandler<PacketConfig>
{
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(PlayerSP player, PacketConfig message)
    {
        if (message.overwrite)
        {
            Config present = MorphLib.proxy.configs.modules.get(message.config.id);

            present.copyServer(message.config);
        }
        else
        {
            Screen screen = Minecraft.getInstance().currentScreen;

            if (screen instanceof GuiDashboard)
            {
                GuiConfigPanel panel = ((GuiDashboard) screen).config;

                panel.storeServerConfig(message.config);
            }
        }
    }
}
