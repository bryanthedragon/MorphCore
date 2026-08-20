package bryanthedragon.morph.math.functions.limit;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;
import bryanthedragon.morph.utils.math.MathUtils;

public class Clamp extends NNFunction
{
    public Clamp(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double doubleValue()
    {
        return MathUtils.clamp(this.getArg(0).doubleValue(), this.getArg(1).doubleValue(), this.getArg(2).doubleValue());
    }
}