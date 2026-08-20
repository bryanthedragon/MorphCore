package bryanthedragon.morph.client.gui.creative;

import bryanthedragon.morph.client.gui.framework.elements.GuiModelRenderer;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.AbstractMorph;

import net.minecraft.client.Minecraft;

public class GuiMorphRenderer extends GuiModelRenderer
{
    public AbstractMorph morph;

    public GuiMorphRenderer(Minecraft mc)
    {
        super(mc);
    }

    @Override
    protected void drawUserModel(GuiContext context)
    {
        if (this.morph == null)
        {
            return;
        }

        MorphUtils.render(this.morph, this.entity, 0, 0, 0, this.yaw, context.partialTicks);
    }
}