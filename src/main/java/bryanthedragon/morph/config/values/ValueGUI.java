package bryanthedragon.morph.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.config.gui.GuiConfigPanel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public abstract class ValueGUI extends Value implements IConfigGuiProvider
{
    public ValueGUI(String id)
    {
        super(id);
    }

    @Override
    public Object getValue()
    {
        return null;
    }

    @Override
    public void setValue(Object value)
    {}

    @Override
    public void reset()
    {}

    @Override
    public void valueFromJSON(JsonElement element)
    {}

    @Override
    public void copy(Value value)
    {}

    @Override
    public JsonElement valueToJSON()
    {
        return JsonNull.INSTANCE;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {}

    @Override
    public void toBytes(ByteBuf buffer)
    {}
}