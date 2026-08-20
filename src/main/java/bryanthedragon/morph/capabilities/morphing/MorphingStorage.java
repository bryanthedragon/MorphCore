package bryanthedragon.morph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.capabilities.CapabilityStorage;
import bryanthedragon.morph.capabilities.CapabilityStorage.IStorage;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/**
 * Morphing storage
 *
 * This class is responsible for saving IMorphing capability to... Hey, Houston,
 * where these data are getting saved? Basically, I don't know.
 *
 * Further research in Minecraft sources shows that capabilities are stored
 * in target's NBT (i.e. ItemStack's, TE's or Entity's NBT) in field "ForgeCaps."
 */
public class MorphingStorage implements IStorage<IMorphing>
{
    @Override
    public Tag writeNBT(CapabilityStorage<IMorphing> capability, IMorphing instance, Direction side)
    {
        CompoundTag tag = new CompoundTag();
        ListTag acquired = new ListTag();

        tag.putFloat("lastHealthRatio", instance.getLastHealthRatio());
        tag.putBoolean("HasSquidAir", instance.getHasSquidAir());
        tag.putInt("SquidAir", instance.getSquidAir());
        tag.putFloat("lastHealth", instance.getLastHealth());

        if (instance.getCurrentMorph() != null)
        {
            CompoundTag morph = new CompoundTag();
            instance.getCurrentMorph().toNBT(morph);

            tag.put("Morph", morph);
        }

        tag.put("Morphs", acquired);

        for (AbstractMorph acquiredMorph : instance.getAcquiredMorphs())
        {
            CompoundTag acquiredTag = new CompoundTag();

            acquiredMorph.toNBT(acquiredTag);
            acquired.add(acquiredTag);
        }

        return tag;
    }

    @Override
    public void readNBT(CapabilityStorage<IMorphing> capability, IMorphing instance, Direction side, Tag nbt)
    {
        if (nbt instanceof CompoundTag)
        {
            CompoundTag tag = (CompoundTag) nbt;
            ListTag acquired = tag.getList("Morphs", 10);
            CompoundTag morphTag = tag.getCompound("Morph");

            instance.setLastHealthRatio(tag.getFloat("LastHealthRatio"));
            instance.setHasSquidAir(tag.getBoolean("HasSquidAir"));
            instance.setSquidAir(tag.getInt("SquidAir"));
            instance.setLastHealth(tag.getFloat("lastHealth"));

            if (!tag.isEmpty())
            {
                instance.setCurrentMorph(MorphManager.INSTANCE.morphFromNBT(morphTag), null, true);
            }

            List<AbstractMorph> acquiredMorphs = new ArrayList<AbstractMorph>();

            if (!acquired.isEmpty())
            {
                for (int i = 0; i < acquired.size(); i++)
                {
                    CompoundTag acquiredTag = acquired.getCompound(i);
                    AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(acquiredTag);

                    if (morph != null)
                    {
                        acquiredMorphs.add(morph);
                    }
                }

                instance.setAcquiredMorphs(acquiredMorphs);
            }
        }
    }
}
