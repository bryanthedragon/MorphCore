package org.bryanthedragon.morph.api.editors.panels;

import org.bryanthedragon.morph.client.gui.framework.elements.input.GuiTextElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.api.morphs.main.PlayerMorph;
import org.bryanthedragon.morph.client.gui.editor.GuiMorphPanel;
import org.bryanthedragon.morph.api.editors.GuiPlayerMorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Username morph panel allows editing username of the player morph 
 */
@OnlyIn(Dist.CLIENT)
public class GuiUsernamePanel extends GuiMorphPanel<PlayerMorph, GuiPlayerMorph>
{
    public GuiTextElement username;
    private int counter = -1;

    public GuiUsernamePanel(Minecraft mc, GuiPlayerMorph editor)
    {
        super(mc, editor);

        this.username = new GuiTextElement(mc, 120, (str) -> this.editUsername(str));
        this.username.flex().relative(this).set(10, 10, 0, 20).w(1, -20).y(1, -30);

        this.add(this.username);
    }

    private void editUsername(String str)
    {
        if (str.isEmpty())
        {
            return;
        }

        this.counter = 15;
    }

    @Override
    public void fillData(PlayerMorph morph)
    {
        super.fillData(morph);

        this.username.setText(morph.profile.getName());
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.counter >= 0)
        {
            if (this.counter == 0 && !this.username.field.getValue().isEmpty())
            {
                PlayerMorph morph = this.morph;

                morph.resetEntity();
                morph.setProfile(this.username.field.getValue());
            }

            String updating = I18n.get("morphcore.gui.panels.updating");
            int w = this.font.width(updating);

            context.graphics.drawString(this.font, updating, this.username.area.ex() - w, this.username.area.y - 12, 0xaaaaaa, true);
            this.counter--;
        }

        super.draw(context);

        if (this.username.isVisible())
        {
            context.graphics.drawString(this.font, I18n.get("morphcore.gui.panels.username"), this.username.area.x, this.username.area.y - 12, 0xffffff, true);
        }
    }
}