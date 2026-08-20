package bryanthedragon.morph.capabilities.morphing;

import java.util.ArrayList;
import java.util.List;

import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.morphs.AbstractMorph;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import net.minecraftforge.common.capabilities.Capability;

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
    public Tag writeNBT(Capability<IMorphing> capability, IMorphing instance, EnumFacing side)
    {
        CompoundTag tag = new CompoundTag();
        ListTag acquired = new ListTag();

        tag.setFloat("lastHealthRatio", instance.getLastHealthRatio());
        tag.setBoolean("HasSquidAir", instance.getHasSquidAir());
        tag.setInteger("SquidAir", instance.getSquidAir());
        tag.setFloat("lastHealth", instance.getLastHealth());

        if (instance.getCurrentMorph() != null)
        {
            CompoundTag morph = new CompoundTag();
            instance.getCurrentMorph().toNBT(morph);

            tag.setTag("Morph", morph);
        }

        tag.setTag("Morphs", acquired);

        for (AbstractMorph acquiredMorph : instance.getAcquiredMorphs())
        {
            CompoundTag acquiredTag = new CompoundTag();

            acquiredMorph.toNBT(acquiredTag);
            acquired.appendTag(acquiredTag);
        }

        return tag;
    }

    @Override
    public void readNBT(Capability<IMorphing> capability, IMorphing instance, EnumFacing side, Tag nbt)
    {
        if (nbt instanceof CompoundTag)
        {
            CompoundTag tag = (CompoundTag) nbt;
            ListTag acquired = tag.getTagList("Morphs", 10);
            CompoundTag morphTag = tag.getCompoundTag("Morph");

            instance.setLastHealthRatio(tag.getFloat("LastHealthRatio"));
            instance.setHasSquidAir(tag.getBoolean("HasSquidAir"));
            instance.setSquidAir(tag.getInteger("SquidAir"));
            instance.setLastHealth(tag.getFloat("lastHealth"));

            if (!tag.hasNoTags())
            {
                instance.setCurrentMorph(MorphManager.INSTANCE.morphFromNBT(morphTag), null, true);
            }

            List<AbstractMorph> acquiredMorphs = new ArrayList<AbstractMorph>();

            if (!acquired.hasNoTags())
            {
                for (int i = 0; i < acquired.tagCount(); i++)
                {
                    CompoundTag acquiredTag = acquired.getCompoundTagAt(i);
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
