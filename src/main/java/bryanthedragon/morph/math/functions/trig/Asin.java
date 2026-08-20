package bryanthedragon.morph.math.functions.trig;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;

public class Asin extends NNFunction
{
    public Asin(IValue[] values, String name) throws Exception
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
        return Math.asin(this.getArg(0).doubleValue());
    }
}
