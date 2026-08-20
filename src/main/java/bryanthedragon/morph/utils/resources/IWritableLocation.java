package bryanthedragon.morph.utils.resources;

import com.google.gson.JsonElement;

import bryanthedragon.morph.utils.ICopy;

import net.minecraft.nbt.Tag;

public interface IWritableLocation<T> extends ICopy<T>
{
    public void fromNbt(Tag nbt) throws Exception;

    public void fromJson(JsonElement element) throws Exception;

    public Tag writeNbt();

    public JsonElement writeJson();
}