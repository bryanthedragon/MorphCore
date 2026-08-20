package bryanthedragon.morph.capabilities.render;

import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class ModelProvider implements ICapabilityProvider
{
    public static final Capability<IModelRenderer> MODEL = CapabilityManager.get(new CapabilityToken<IModelRenderer>() {});

    private final LazyOptional<IModelRenderer> instance = LazyOptional.of(ModelRenderer::new);

    public boolean hasCapability(Capability<?> capability, Direction facing)
    {
        return capability == MODEL;
    }

    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing)
    {
        return capability == MODEL ? this.instance.cast() : LazyOptional.empty();
    }
}