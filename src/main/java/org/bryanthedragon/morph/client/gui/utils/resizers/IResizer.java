package org.bryanthedragon.morph.client.gui.utils.resizers;

import org.bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import org.bryanthedragon.morph.client.gui.utils.Area;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResizer
{
    public void preApply(Area area);

    public void apply(Area area);

    public void postApply(Area area);

    @OnlyIn(Dist.CLIENT)
    public void add(GuiElement parent, GuiElement child);

    @OnlyIn(Dist.CLIENT)
    public void remove(GuiElement parent, GuiElement child);

    public int getX();

    public int getY();

    public int getW();

    public int getH();
}