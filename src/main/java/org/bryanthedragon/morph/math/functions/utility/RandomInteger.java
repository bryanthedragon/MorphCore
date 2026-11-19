package org.bryanthedragon.morph.math.functions.utility;

import org.bryanthedragon.morph.math.IValue;

public class RandomInteger extends Random
{
    public RandomInteger(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return (int) super.doubleValue();
    }
}