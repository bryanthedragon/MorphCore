package org.bryanthedragon.morph.capabilities.render;

import org.bryanthedragon.morph.capabilities.CapabilityStorage;
import org.bryanthedragon.morph.capabilities.CapabilityStorage.IStorage;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

public class ModelRendererStorage implements IStorage<IModelRenderer>
{
    public Tag writeNBT(CapabilityStorage<IModelRenderer> capability, IModelRenderer instance, Direction side)
    {
        return new CompoundTag();
    }

    public void readNBT(CapabilityStorage<IModelRenderer> capability, IModelRenderer instance, Direction side, Tag nbt)
    {
        
    }
}