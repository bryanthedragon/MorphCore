package bryanthedragon.morph.math.functions.limit;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;

public class Max extends NNFunction
{
    public Max(IValue[] values, String name) throws Exception
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
        return Math.max(this.getArg(0).doubleValue(), this.getArg(1).doubleValue());
    }
}