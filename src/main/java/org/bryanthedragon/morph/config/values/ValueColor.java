package bryanthedragon.morph.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import bryanthedragon.morph.utils.color.Color;
import bryanthedragon.morph.utils.interpolation.Interpolation;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntTag;

import javax.annotation.Nullable;

public class ValueColor extends GenericValue<Color>
{
    public ValueColor(String id)
    {
        super(id);
    }

    public ValueColor(String id, Color defaultValue)
    {
        super(id, defaultValue);
    }

    @Override
    public GenericBaseValue<Color> copy()
    {
        ValueColor clone = new ValueColor(this.id, this.defaultValue);
        clone.value = this.value;
        return clone;
    }

    @Override
    public void copy(Value origin)
    {
        if (origin instanceof ValueColor)
        {
            this.set(((ValueColor) origin).value);
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);

        this.defaultValue = new Color(buffer.readInt());
        this.valueFromBytes(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);

        buffer.writeInt(this.defaultValue.getRGBAColor());
        this.valueToBytes(buffer);
    }

    @Override
    public void valueFromBytes(ByteBuf buffer)
    {
        this.value = new Color(buffer.readInt());
    }

    @Override
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.value.getRGBAColor());
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(new Color(element.getAsInt()));
    }

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value.getRGBAColor());
    }

    @Override
    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof IntTag)
        {
            this.set(new Color(((IntTag) tag).getAsInt()));
        }
    }

    @Nullable
    @Override
    public Tag valueToNBT()
    {
        return IntTag.valueOf(this.value.getRGBAColor());
    }

    public Color interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Color)) return this.value.copy();

        Color toC = (Color) to.value;
        Color interpolated = new Color();
        interpolated.r = interpolation.interpolate(this.value.r, toC.r, factor);
        interpolated.g = interpolation.interpolate(this.value.g, toC.g, factor);
        interpolated.b = interpolation.interpolate(this.value.b, toC.b, factor);
        interpolated.a = interpolation.interpolate(this.value.a, toC.a, factor);

        return interpolated;
    }
}
