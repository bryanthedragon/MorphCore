package org.bryanthedragon.morph.client.gui.creative;

import org.bryanthedragon.morph.client.gui.framework.elements.GuiModelRenderer;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.api.utils.morph.MorphUtils;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;

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