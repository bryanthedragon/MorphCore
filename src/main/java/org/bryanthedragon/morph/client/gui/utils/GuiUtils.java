package org.bryanthedragon.morph.client.gui.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * GUI utilities
 */
@OnlyIn(Dist.CLIENT)
public class GuiUtils
{
    /**
     * Draw an entity on the screen.
     */
    public static void drawEntityOnScreen(GuiGraphics graphics, int posX, int posY, int scale, int mouseX, int mouseY, LivingEntity ent)
    {
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, posX, posY, scale, mouseX, mouseY, ent);
    }

    public static void drawEntityOnScreen(GuiGraphics graphics, int posX, int posY, int scale, LivingEntity ent)
    {
        InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, posX, posY, scale, 0.0F, 0.0F, ent);
    }


    /**
     * Open web link
     */
    public static void openWebLink(String address)
    {
        try
        {
            Util.getPlatform().openUri(new URI(address));
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Invalid URI: " + address, e);
        }
    }

    /**
     * Open a URL
     */
    public static void openWebLink(URI uri)
    {
        Util.getPlatform().openUri(uri);
    }

    public static void playClick()
    {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Open a folder in the operating system's file browser.
     */
    public static void openFolder(String url)
    {
        File file = new File(url);

        Util.getPlatform().openFile(file);
    }
}