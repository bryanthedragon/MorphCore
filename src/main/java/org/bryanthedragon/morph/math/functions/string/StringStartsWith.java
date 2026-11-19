package org.bryanthedragon.morph.math.functions.string;

import org.bryanthedragon.morph.math.IValue;
import org.bryanthedragon.morph.math.functions.SNFunction;

public class StringStartsWith extends SNFunction
{
    public StringStartsWith(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 2;
    }

    @Override
    public double doubleValue()
    {
        return this.getArg(0).stringValue().startsWith(this.getArg(1).stringValue()) ? 1 : 0;
    }
}
