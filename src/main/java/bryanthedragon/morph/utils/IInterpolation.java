package bryanthedragon.morph.utils;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IInterpolation
{
    public float interpolate(float a, float b, float x);

    public double interpolate(double a, double b, double x);

    @OnlyIn(Dist.CLIENT)
    public default String getName()
    {
        return I18n.format(this.getKey());
    }

    @OnlyIn(Dist.CLIENT)
    public String getKey();

    @OnlyIn(Dist.CLIENT)
    public default String getTooltip()
    {
        return I18n.format(this.getTooltipKey());
    }

    @OnlyIn(Dist.CLIENT)
    public String getTooltipKey();
}
