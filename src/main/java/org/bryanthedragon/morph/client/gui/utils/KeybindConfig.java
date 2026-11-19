package org.bryanthedragon.morph.client.gui.utils;

import java.io.File;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.config.Config;
import org.bryanthedragon.morph.config.json.ConfigParser;
import org.bryanthedragon.morph.config.values.Value;
import org.bryanthedragon.morph.config.values.ValueInt;
import org.bryanthedragon.morph.utils.key.Keys;

import net.minecraftforge.fml.ModList;

public class KeybindConfig extends Config
{
    public transient Map<String, IKey> keyMap;

    public KeybindConfig()
    {
        super("keybinds", new File(MorphLib.proxy.configFolder, "morphlib/keybinds.json"));

        this.load();
    }

    public void addKeybind(Keybind key)
    {
        if (Keys.getMainKey(key.keyCode) == GLFW.GLFW_KEY_ESCAPE)
        {
            return;
        }

        String modid = key.modid;
        ModKeybinds mod = (ModKeybinds) this.values.get(modid);
        
        mod.addKeybind(key);
    }

    public void updateCategory(Keybind key, IKey categoryKey)
    {
        if (key.category != IKey.EMPTY)
        {
            return;
        }

        Value category = this.values.get(key.modid).getSubValue("");

        category.removeSubValue(key.labelToken);
        key.setCategory(categoryKey);
        this.addKeybind(key);
    }

    public ValueInt getKeybind(String modid, String categoryId, String id)
    {
        Value category = this.get(modid, categoryId);

        if (category != null)
        {
            return (ValueInt) category.getSubValue(id);
        }
        else
        {
            return null;
        }
    }

    public void load()
    {
        ModList.get().forEachModContainer((modid, container) ->
        {
            Value mod = new ModKeybinds(container);

            mod.setConfig(this);

            this.values.put(mod.id, mod);
        });

        ModKeybinds modKeybinds = new ModKeybinds(null);

        modKeybinds.setConfig(this);

        this.values.put("", modKeybinds);

        ConfigParser.fromJson(this, this.file);
    }

    @Override
    public String getCategoryTitleKey(Value value)
    {
        return value.getLabelKey();
    }

    @Override
    public String getCategoryTooltipKey(Value value)
    {
        return "";
    }

    @Override
    public String getValueLabelKey(Value value)
    {
        return "";
    }

    @Override
    public String getValueCommentKey(Value value)
    {
        return "";
    }
}
