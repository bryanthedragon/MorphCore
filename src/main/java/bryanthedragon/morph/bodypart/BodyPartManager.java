package bryanthedragon.morph.bodypart;

import java.util.ArrayList;
import java.util.List;

import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.utils.ISyncableMorph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Body part manager
 * 
 * Simplifies management of body parts for mods that use it. Besides 
 * that it allows in the future to implement different types of body 
 * parts such as particle body part...
 */
public class BodyPartManager
{
    /**
     * List of body parts (on client side only)
     */
    public List<BodyPart> parts = new ArrayList<BodyPart>();

    /**
     * Whether body parts were initiated 
     */
    private boolean initiated;

    /**
     * Reset initiated state
     */
    public void reset()
    {
        this.initiated = false;
    }

    @OnlyIn(Dist.CLIENT)
    public void reinitBodyParts()
    {
        this.reset();
        this.initBodyParts();
    }

    @OnlyIn(Dist.CLIENT)
    public void initBodyParts()
    {
        if (!this.initiated)
        {
            for (BodyPart part : this.parts)
            {
                part.init();
            }

            this.initiated = true;
        }
    }

    /**
     * Update body limbs 
     */
    public void updateBodyLimbs(AbstractMorph parent, LivingEntity target)
    {
        for (BodyPart part : this.parts)
        {
            part.update(parent, target);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BodyPartManager)
        {
            BodyPartManager manager = (BodyPartManager) obj;

            return this.parts.equals(manager.parts);
        }

        return super.equals(obj);
    }

    public void copy(BodyPartManager manager)
    {
        this.reset();
        this.parts.clear();

        for (BodyPart part : manager.parts)
        {
            this.parts.add(part.copy());
        }
    }

    public void merge(BodyPartManager manager)
    {
        this.initiated = false;

        for (int i = 0, c = manager.parts.size(); i < c; i++)
        {
            BodyPart part = i < this.parts.size() ? this.parts.get(i) : null;
            BodyPart other = manager.parts.get(i);

            if (part == null)
            {
                this.parts.add(other);
            }
            else if (!part.canMerge(other))
            {
                this.parts.set(i, other.copy());
            }
        }

        this.ensureCount(manager.parts.size());
    }

    public void afterMerge(BodyPartManager manager)
    {
        if (manager.parts.size() != this.parts.size())
        {
            this.copy(manager);

            return;
        }

        for (int i = 0, c = this.parts.size(); i < c; i++)
        {
            BodyPart part = this.parts.get(i);
            BodyPart other = manager.parts.get(i);

            if (part.morph.isEmpty() || other.morph.isEmpty())
            {
                continue;
            }

            part.morph.get().afterMerge(other.morph.get());
        }
    }

    public void pause(AbstractMorph previous, int offset)
    {
        BodyPartManager parts = previous instanceof IBodyPartProvider ? ((IBodyPartProvider) previous).getBodyPart() : null;

        for (int i = 0; i < this.parts.size(); i++)
        {
            BodyPart current = this.parts.get(i);
            BodyPart past = null;

            if (parts != null)
            {
                past = i < parts.parts.size() ? parts.parts.get(i) : null;
            }

            current.pause(past, offset);
        }
    }

    private void ensureCount(int count)
    {
        count = Math.max(count, 0);

        while (this.parts.size() > count)
        {
            this.parts.remove(this.parts.size() - 1);
        }
    }

    /* NBT */

    public ListTag toNBT()
    {
        if (!this.parts.isEmpty())
        {
            ListTag bodyParts = new ListTag();

            for (BodyPart part : this.parts)
            {
                CompoundTag bodyPart = new CompoundTag();

                part.toNBT(bodyPart);

                if (!bodyPart.hasNoTags())
                {
                    bodyParts.appendTag(bodyPart);
                }
            }

            return bodyParts;
        }

        return null;
    }

    public void fromNBT(ListTag bodyParts)
    {
        this.parts.clear();

        for (int i = 0, c = bodyParts.tagCount(); i < c; i++)
        {
            CompoundTag bodyPart = bodyParts.getCompound(i);
            BodyPart part = new BodyPart();

            part.fromNBT(bodyPart);
            this.parts.add(part);
        }
    }
}