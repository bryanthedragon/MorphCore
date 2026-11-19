package org.bryanthedragon.morph.network.morphlib.client;

import org.bryanthedragon.morph.client.gui.framework.elements.GuiConfirmationScreen;
import org.bryanthedragon.morph.network.ClientMessageHandler;
import org.bryanthedragon.morph.network.morphlib.Dispatcher;
import org.bryanthedragon.morph.network.morphlib.common.PacketConfirm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ClientHandlerConfirm extends ClientMessageHandler<PacketConfirm>
{

    /**
     * Renders the GUI based on the enum value of packet. Every GUI confirmation screen dispatches the packet back to the server
     * @param packet
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void run(LocalPlayer LocalPlayer, PacketConfirm packet)
    {
        switch(packet.gui)
        {
            case MCSCREEN: 
                Minecraft.getInstance().setScreen(new GuiConfirmationScreen(packet.langKey, (value) -> { this.dispatchPacket(packet, value); }));
        }
    }

    private void dispatchPacket(PacketConfirm packet, boolean value)
    {
        packet.confirm = value;

        Dispatcher.sendToServer(packet);
    }

    public enum GUI {
        MCSCREEN;
    }
}
