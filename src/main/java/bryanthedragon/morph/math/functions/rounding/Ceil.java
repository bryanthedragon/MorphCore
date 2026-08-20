package bryanthedragon.morph.math.functions.rounding;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;

public class Ceil extends NNFunction
{
    public Ceil(IValue[] values, String name) throws Exception
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
        return Math.ceil(this.getArg(0).doubleValue());
    }
}