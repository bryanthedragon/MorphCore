package bryanthedragon.morph.math.functions.trig;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;

public class Atan2 extends NNFunction
{
    public Atan2(IValue[] values, String name) throws Exception
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
        return Math.atan2(this.getArg(0).doubleValue(), this.getArg(1).doubleValue());
    }
}
