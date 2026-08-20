package bryanthedragon.morph.math.molang.functions;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.trig.Atan;

public class AtanDegrees extends Atan
{
    public AtanDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}