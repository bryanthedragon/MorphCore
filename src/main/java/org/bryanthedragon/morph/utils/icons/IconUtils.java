package org.bryanthedragon.morph.utils.icons;

import org.bryanthedragon.morph.client.gui.utils.Icon;

import net.minecraft.resources.ResourceLocation;

/**
 * MorphCore icons
 */

public class IconUtils
{
    public static class MMIcons
    {
        public static final ResourceLocation PANEL_ICONS = new ResourceLocation("morphcore:textures/gui/icons.png");

        public static final Icon USER = new Icon(PANEL_ICONS, 0, 0);
        public static final Icon PROPERTIES = new Icon(PANEL_ICONS, 16, 0);
        public static final Icon ITEM = new Icon(PANEL_ICONS, 32, 0);
        public static final Icon LABEL = new Icon(PANEL_ICONS, 48, 0);
    }
}