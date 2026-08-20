package bryanthedragon.morph.client;

import javax.swing.text.JTextComponent.KeyBinding;

import bryanthedragon.morph.ClientProxy;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.client.gui.creative.GuiCreativeScreen;
import bryanthedragon.morph.client.gui.creative.GuiSelectorsScreen;
import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.morph.network.common.survival.PacketAction;
import bryanthedragon.morph.network.common.survival.PacketSelectMorph;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Keyboard handler 
 * 
 * This class (handler) is responsible for handling the keyboard input for 
 * executing an action and morphing the player using survival morphing menu.
 * 
 * This handler is also responsible for opening up creative morphing menu.
 */
public class KeyboardHandler
{
    /* Action key */
    private KeyBinding keyAction;
    private KeyBinding keyCreativeMenu;
    private KeyBinding keySelectorMenu;
    private KeyBinding keySurvivalMenu;

    /* Morph related keys */
    public KeyBinding keyDemorph;

    public KeyboardHandler()
    {
        String category = "key.morphcore";

        /* Create key bindings */
        keyAction = new KeyBinding("key.morphcore.action", Keyboard.KEY_V, category);
        keyCreativeMenu = new KeyBinding("key.morphcore.creative_menu", Keyboard.KEY_B, category);
        keySelectorMenu = new KeyBinding("key.morphcore.selector_menu", Keyboard.KEY_MINUS, category);
        keySurvivalMenu = new KeyBinding("key.morphcore.survival_menu", Keyboard.KEY_X, category);

        keyDemorph = new KeyBinding("key.morphcore.demorph", Keyboard.KEY_PERIOD, category);

        /* Register them in the client registry */
        ClientRegistry.registerKeyBinding(keyAction);
        ClientRegistry.registerKeyBinding(keyCreativeMenu);
        ClientRegistry.registerKeyBinding(keySelectorMenu);
        ClientRegistry.registerKeyBinding(keySurvivalMenu);

        ClientRegistry.registerKeyBinding(keyDemorph);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        IMorphing morphing = Morphing.get(player);

        boolean wasUsed = false;
        boolean spectator = player.isSpectator();

        if (this.keyCreativeMenu.isPressed() && MorphCore.proxy.canUse(player))
        {
            mc.displayGuiScreen(new GuiCreativeScreen(mc));
            wasUsed = true;
        }

        if (this.keySelectorMenu.isPressed() && MorphCore.proxy.canEditSelectors())
        {
            mc.displayGuiScreen(new GuiSelectorsScreen(mc));
            wasUsed = true;
        }

        /* Action */
        if (this.keyAction.isPressed() && !spectator)
        {
            Dispatcher.sendToServer(new PacketAction());

            if (morphing != null && morphing.isMorphed())
            {
                morphing.getCurrentMorph().action(player);
                wasUsed = true;
            }
        }

        /* Survival morphing key handling */
        if (this.keySurvivalMenu.isPressed() && !spectator)
        {
            mc.displayGuiScreen(ClientProxy.getSurvivalScreen().open());
            wasUsed = true;
        }

        /* Demorph from current morph */
        if (this.keyDemorph.isPressed() && !spectator)
        {
            if (morphing != null && morphing.isMorphed())
            {
                Dispatcher.sendToServer(new PacketSelectMorph(-1));
                wasUsed = true;
            }
        }

        if (!wasUsed && Keyboard.getEventKeyState())
        {
            int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            MorphManager.INSTANCE.list.keyTyped(player, key);
        }
    }
}