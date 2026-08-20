package bryanthedragon.morph.math.functions.utility;

import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.functions.NNFunction;
import bryanthedragon.morph.utils.interpolation.Interpolations;

public class LerpRotate extends NNFunction
{
    public LerpRotate(IValue[] values, String name) throws Exception
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
        return Interpolations.lerpYaw(this.getArg(0).doubleValue(), this.getArg(1).doubleValue(), this.getArg(2).doubleValue());
    }
}