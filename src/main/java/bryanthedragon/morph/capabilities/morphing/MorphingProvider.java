package bryanthedragon.morph.capabilities.morphing;

import net.minecraft.nbt.Tag;
import bryanthedragon.morph.capabilities.CapabilityInject;
import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.CapabilityProvider;

/**
 * Morhping capability provider
 *
 * Now that I understand capability system, it seems pretty easy to use!
 */
public class MorphingProvider implements ICapabilitySerializable<Tag>
{
    @CapabilityInject(IMorphing.class)
    public static final Capability<IMorphing> MORPHING_CAP = null;

    private IMorphing instance = MORPHING_CAP.getDefaultInstance();

    public boolean hasCapability(Capability<?> capability, Direction facing)
    {
        return capability == MORPHING_CAP;
    }
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing)
    {
        return capability == MORPHING_CAP ? MORPHING_CAP.<T> cast(this.instance) : LazyOptional.empty();
    }

    public Tag serializeNBT()
    {
        return MORPHING_CAP.getStorage().writeNBT(MORPHING_CAP, this.instance, null);
    }

    public void deserializeNBT(Tag nbt)
    {
        MORPHING_CAP.getStorage().readNBT(MORPHING_CAP, this.instance, null, nbt);
    }
}