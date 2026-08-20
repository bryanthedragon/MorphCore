package bryanthedragon.morph.math.functions.classic;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;

/**
 * Absolute value function 
 */
public class Abs extends NNFunction
{
    public Abs(IValue[] values, String name) throws Exception
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
        return Math.abs(this.getArg(0).doubleValue());
    }
}