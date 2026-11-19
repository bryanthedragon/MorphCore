package org.bryanthedragon.morph.config.values;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;

import org.bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import org.bryanthedragon.morph.client.gui.framework.elements.input.GuiTrackpadElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiLabel;
import org.bryanthedragon.morph.client.gui.utils.Elements;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.config.gui.GuiConfigPanel;
import org.bryanthedragon.morph.utils.interpolation.Interpolation;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

public class ValueLong extends GenericNumberValue<Long> implements IServerValue, IConfigGuiProvider
{
    public ValueLong(String id)
    {
        super(id, 0L, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public ValueLong(String id, long defaultValue)
    {
        super(id, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public ValueLong(String id, long defaultValue, long min, long max)
    {
        super(id, defaultValue, min, max);
    }

    @Override
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    protected Long getNullValue()
    {
        return 0L;
    }

    public boolean isInteger()
    {
        return true;
    }

    @Override
    protected Long numberToValue(Number number) {
        return number.longValue();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getLabelKey()), 0).anchor(0, 0.5F);

        element.flex().row(0).preferred(0).height(20);
        element.add(label);

        GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

        trackpad.flex().w(90);
        element.add(trackpad.removeTooltip());

        return Arrays.asList(element.tooltip(IKey.lang(this.getCommentKey())));
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsLong());
    }

    @Override
    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof NumericTag)
        {
            this.set(((NumericTag) tag).getAsLong());
        }
    }

    @Override
    public Tag valueToNBT()
    {
        return LongTag.valueOf(this.value);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        try
        {
            this.set(Long.parseLong(value));

            return true;
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueLong)
        {
            this.set(((ValueLong) value).value);
        }
    }

    @Override
    public void copyServer(Value value)
    {
        if (value instanceof ValueLong)
        {
            this.serverValue = ((ValueLong) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);

        this.defaultValue = buffer.readLong();
        this.min = buffer.readLong();
        this.max = buffer.readLong();
        this.valueFromBytes(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);

        buffer.writeLong(this.defaultValue);
        buffer.writeLong(this.min);
        buffer.writeLong(this.max);
        this.valueToBytes(buffer);
    }

    @Override
    public void valueFromBytes(ByteBuf buffer)
    {
        this.set(buffer.readLong());
    }

    @Override
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeLong(this.value);
    }

    @Override
    public ValueLong copy()
    {
        ValueLong clone = new ValueLong(this.id, this.defaultValue, this.min, this.max);
        clone.value = this.value;

        return clone;
    }

    @Override
    public String toString()
    {
        return Long.toString(this.value);
    }

    public Long interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Long)) return this.value;

        return (long) interpolation.interpolate(this.value, (Long) to.value, factor);
    }
}