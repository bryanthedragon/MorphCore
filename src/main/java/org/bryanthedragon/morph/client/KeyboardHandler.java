package org.bryanthedragon.morph.client;

import com.mojang.blaze3d.platform.InputConstants;

import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.MorphCoreProxy.ClientCoreProxy;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.client.gui.creative.GuiCreativeScreen;
import org.bryanthedragon.morph.client.gui.creative.GuiSelectorsScreen;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.common.survival.PacketAction;
import org.bryanthedragon.morph.network.common.survival.PacketSelectMorph;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;

/**
 * Handles client keyboard input for morph actions and menus.
 */
@Mod.EventBusSubscriber(modid = MorphCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyboardHandler
{
    private static final String CATEGORY = "key.morphcore";
    private static final KeyMapping KEY_ACTION = new KeyMapping("key.morphcore.action", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    private static final KeyMapping KEY_CREATIVE_MENU = new KeyMapping("key.morphcore.creative_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
    private static final KeyMapping KEY_SELECTOR_MENU = new KeyMapping("key.morphcore.selector_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_MINUS, CATEGORY);
    private static final KeyMapping KEY_SURVIVAL_MENU = new KeyMapping("key.morphcore.survival_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);
    public static final KeyMapping keyDemorph = new KeyMapping("key.morphcore.demorph", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, CATEGORY);

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(KEY_ACTION);
        event.register(KEY_CREATIVE_MENU);
        event.register(KEY_SELECTOR_MENU);
        event.register(KEY_SURVIVAL_MENU);
        event.register(keyDemorph);
    }

    @SubscribeEvent
    public void onKey(InputEvent.Key event)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null)
        {
            return;
        }

        IMorphing morphing = Morphing.get(player);
        boolean wasUsed = false;
        boolean spectator = player.isSpectator();

        if (KEY_CREATIVE_MENU.consumeClick() && MorphCore.proxy.canUse(player))
        {
            mc.setScreen(new GuiCreativeScreen(mc));
            wasUsed = true;
        }

        if (KEY_SELECTOR_MENU.consumeClick() && MorphCore.proxy.canEditSelectors())
        {
            mc.setScreen(new GuiSelectorsScreen(mc));
            wasUsed = true;
        }

        if (KEY_ACTION.consumeClick() && !spectator)
        {
            Dispatcher.sendToServer(new PacketAction());

            if (morphing != null && morphing.isMorphed())
            {
                morphing.getCurrentMorph().action(player);
                wasUsed = true;
            }
        }

        if (KEY_SURVIVAL_MENU.consumeClick() && !spectator)
        {
            mc.setScreen(ClientCoreProxy.getSurvivalScreen().open());
            wasUsed = true;
        }

        if (keyDemorph.consumeClick() && !spectator && morphing != null && morphing.isMorphed())
        {
            Dispatcher.sendToServer(new PacketSelectMorph(-1));
            wasUsed = true;
        }

        if (!wasUsed && event.getAction() == GLFW.GLFW_PRESS && event.getKey() != GLFW.GLFW_KEY_UNKNOWN)
        {
            MorphManager.INSTANCE.list.keyTyped(player, event.getKey());
        }
    }
}