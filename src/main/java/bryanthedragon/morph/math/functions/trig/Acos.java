package bryanthedragon.morph.math.functions.trig;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;

public class Acos extends NNFunction
{
    public Acos(IValue[] values, String name) throws Exception
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
        return Math.acos(this.getArg(0).doubleValue());
    }
}
