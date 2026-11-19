package org.bryanthedragon.morph.math.molang.functions;

import org.bryanthedragon.morph.math.IValue;
import org.bryanthedragon.morph.math.functions.trig.Acos;

public class AcosDegrees extends Acos
{
    public AcosDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}