package org.bryanthedragon.morph.api.morphs.utils;

import org.bryanthedragon.morph.utils.math.MathUtils;
import org.bryanthedragon.morph.utils.interpolation.Interpolation;
import net.minecraft.nbt.CompoundTag;

public class Animation
{
    public boolean animates;
    public boolean ignored;
    public int duration = 10;
    public Interpolation interp = Interpolation.LINEAR;

    public int progress;
    public boolean paused;

    public void pause()
    {
        this.pause(0);
    }

    public void pause(int progress)
    {
        this.paused = true;
        this.progress = progress;
    }

    public float getFactor(float partialTicks)
    {
        if (!this.animates || this.duration <= 0)
        {
            return 1F;
        }

        return MathUtils.clamp((this.progress + (this.paused ? 0 : partialTicks)) / (float) this.duration, 0F, 1F);
    }

    public void reset()
    {
        this.progress = this.duration;
    }

    public void merge(Animation animation)
    {
        this.copy(animation);
        this.progress = 0;
    }

    public void copy(Animation animation)
    {
        this.animates = animation.animates;
        this.duration = animation.duration;
        this.interp = animation.interp;
        this.ignored = animation.ignored;
        this.paused = animation.paused;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Animation)
        {
            Animation animation = (Animation) obj;

            return this.animates == animation.animates &&
                this.duration == animation.duration &&
                this.ignored == animation.ignored &&
                this.interp == animation.interp;
        }

        return super.equals(obj);
    }

    public void update()
    {
        if (this.animates && !this.paused)
        {
            this.progress++;
        }
    }

    public boolean isInProgress()
    {
        return this.animates && (this.paused || this.progress < this.duration);
    }

    public CompoundTag toNBT()
    {
        CompoundTag tag = new CompoundTag();

        if (this.animates) tag.putBoolean("Animates", this.animates);
        if (this.ignored) tag.putBoolean("Ignored", this.ignored);
        if (this.duration != 10) tag.putInt("Duration", this.duration);
        if (this.interp != Interpolation.LINEAR) tag.putInt("Interp", this.interp.ordinal());

        return tag;
    }

    public void fromNBT(CompoundTag tag)
    {
        if (tag.contains("Animates")) this.animates = tag.getBoolean("Animates");
        if (tag.contains("Ignored")) this.ignored = tag.getBoolean("Ignored");
        if (tag.contains("Duration")) this.duration = tag.getInt("Duration");
        if (tag.contains("Interp")) this.interp = Interpolation.values()[tag.getInt("Interp")];

        this.reset();
    }
}