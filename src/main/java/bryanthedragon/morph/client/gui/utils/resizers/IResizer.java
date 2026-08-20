package bryanthedragon.morph.client.gui.utils.resizers;

import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.utils.Area;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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