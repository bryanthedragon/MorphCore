package bryanthedragon.morph.math.functions.rounding;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;

public class Trunc extends NNFunction
{
    public Trunc(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double doubleValue()
    {
        double value = this.getArg(0).doubleValue();

        return value < 0 ? Math.ceil(value) : Math.floor(value);
    }
}