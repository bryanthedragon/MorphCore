package org.bryanthedragon.morph.api.editors;

import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.api.morphs.main.PlayerMorph;
import org.bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import org.bryanthedragon.morph.utils.icons.IconUtils.MMIcons;
import org.bryanthedragon.morph.api.editors.panels.GuiUsernamePanel;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPlayerMorph extends GuiAbstractMorph<PlayerMorph>
{
    public GuiUsernamePanel username;

    public GuiPlayerMorph(Minecraft mc)
    {
        super(mc);

        this.defaultPanel = this.username = new GuiUsernamePanel(mc, this);
        this.registerPanel(this.username, IKey.lang("morphcore.gui.panels.username"), MMIcons.USER);
    }

    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof PlayerMorph;
    }
}