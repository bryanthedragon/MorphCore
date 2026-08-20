package bryanthedragon.morph.capabilities.render;

import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ModelProvider implements ICapabilityProvider
{
    @CapabilityInject(IModelRenderer.class)
    public static final Capability<IModelRenderer> MODEL = null;

    private IModelRenderer instance = MODEL.getDefaultInstance();

    public boolean hasCapability(Capability<?> capability, Direction facing)
    {
        return capability == MODEL;
    }

    public <T> T getCapability(Capability<T> capability, Direction facing)
    {
        return capability == MODEL ? MODEL.<T>cast(this.instance) : null;
    }
}