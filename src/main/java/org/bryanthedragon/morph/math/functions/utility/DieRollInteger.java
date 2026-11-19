package org.bryanthedragon.morph.math.functions.utility;

import org.bryanthedragon.morph.math.IValue;

public class DieRollInteger extends DieRoll
{
    public DieRollInteger(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return (int) super.doubleValue();
    }
}