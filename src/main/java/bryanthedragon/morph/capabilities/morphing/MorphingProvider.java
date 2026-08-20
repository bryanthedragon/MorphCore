package bryanthedragon.morph.capabilities.morphing;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

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

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == MORPHING_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == MORPHING_CAP ? MORPHING_CAP.<T> cast(this.instance) : null;
    }

    @Override
    public Tag serializeNBT()
    {
        return MORPHING_CAP.getStorage().writeNBT(MORPHING_CAP, this.instance, null);
    }

    @Override
    public void deserializeNBT(Tag nbt)
    {
        MORPHING_CAP.getStorage().readNBT(MORPHING_CAP, this.instance, null, nbt);
    }
}