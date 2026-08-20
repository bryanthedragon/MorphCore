package bryanthedragon.vanilla_pack.editors;

import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.morph.util.MMIcons;
import bryanthedragon.vanilla_pack.editors.panels.GuiUsernamePanel;
import bryanthedragon.vanilla_pack.morphs.PlayerMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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