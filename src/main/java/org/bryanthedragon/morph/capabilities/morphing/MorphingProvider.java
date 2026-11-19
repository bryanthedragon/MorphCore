package org.bryanthedragon.morph.capabilities.morphing;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Morphing capability provider
 *
 * Now that I understand capability system, it seems pretty easy to use!
 */
public class MorphingProvider implements ICapabilitySerializable<Tag>
{
    public static final Capability<IMorphing> MORPHING_CAP = CapabilityManager.get(new CapabilityToken<IMorphing>() {});

    private final IMorphing instance = new Morphing();
    private final LazyOptional<IMorphing> optional = LazyOptional.of(() -> this.instance);
    private final MorphingStorage storage = new MorphingStorage();

    public boolean hasCapability(Capability<?> capability, Direction facing)
    {
        return capability == MORPHING_CAP;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing)
    {
        return capability == MORPHING_CAP ? this.optional.cast() : LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT()
    {
        return this.storage.writeNBT(this.instance);
    }

    @Override
    public void deserializeNBT(Tag nbt)
    {
        this.storage.readNBT(this.instance, nbt);
    }
}