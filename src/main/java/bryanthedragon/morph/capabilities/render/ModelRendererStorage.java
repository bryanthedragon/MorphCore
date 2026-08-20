package bryanthedragon.morph.capabilities.render;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ModelRendererStorage implements IStorage<IModelRenderer>
{
    public Tag writeNBT(Capability<IModelRenderer> capability, IModelRenderer instance, Direction side)
    {
        return new CompoundTag();
    }

    public void readNBT(Capability<IModelRenderer> capability, IModelRenderer instance, Direction side, Tag nbt)
    {
        
    }
}