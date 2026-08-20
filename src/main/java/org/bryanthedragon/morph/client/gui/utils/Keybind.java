package bryanthedragon.morph.client.gui.utils;

import bryanthedragon.morph.MorphLibProxy.ClientLibProxy;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.client.gui.utils.keys.KeyParser;
import bryanthedragon.morph.config.values.ValueInt;
import bryanthedragon.morph.utils.key.Keys;

import java.util.function.Supplier;

/**
 * Keybind class
 */
public class Keybind
{
    public String modid;
    public IKey label;
    public IKey category = IKey.EMPTY;
    public int keyCode;
    public Runnable callback;
    public boolean inside;
    public boolean active = true;
    public Supplier<Boolean> activeSupplier;

    public String labelToken = "";
    public String categoryToken = "";

    public Keybind(String modid, IKey label, int keyCode, Runnable callback)
    {
        this.modid = modid;
        this.label = label;
        this.keyCode = keyCode;
        this.callback = callback;

        this.labelToken = KeyParser.toJson(label);

        ClientLibProxy.keybinds.addKeybind(this);
    }

    public Keybind held(int... keys)
    {
        this.keyCode = Keys.getComboKeyCode(keys, keyCode);

        ClientLibProxy.keybinds.addKeybind(this);

        return this;
    }

    public Keybind inside()
    {
        this.inside = true;

        return this;
    }

    public Keybind active(Supplier<Boolean> active)
    {
        this.activeSupplier = active;

        return this;
    }

    public Keybind active(boolean active)
    {
        this.active = active;

        return this;
    }

    public Keybind category(IKey category)
    {
        ClientLibProxy.keybinds.updateCategory(this, category);

        return this;
    }

    public void setCategory(IKey category)
    {
        this.category = category;
        this.categoryToken = KeyParser.toJson(category);
    }

    public String getKeyCombo()
    {
        ValueInt config = ClientLibProxy.keybinds.getKeybind(this.modid, this.categoryToken, this.labelToken);

        if (config != null)
        {
            return Keys.getComboKeyName(config.get());
        }
        else
        {
            return Keys.getComboKeyName(this.keyCode);
        }
    }

    public boolean check(int keyCode, boolean inside)
    {
        ValueInt config = ClientLibProxy.keybinds.getKeybind(this.modid, this.categoryToken, this.labelToken);
        int check = config == null ? this.keyCode : config.get();

        if (Keys.getMainKey(check) != keyCode || !Keys.checkModifierKeys(check))
        {
            return false;
        }

        if (this.inside)
        {
            return inside;
        }

        return true;
    }

    public boolean isActive()
    {
        if (this.activeSupplier != null)
        {
            return this.activeSupplier.get();
        }

        return this.active;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Keybind)
        {
            Keybind keybind = (Keybind) obj;

            return this.keyCode == keybind.keyCode && this.inside == keybind.inside;
        }

        return super.equals(obj);
    }
}