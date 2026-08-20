package bryanthedragon.morph.api;

import bryanthedragon.morph.api.abilities.IAbility;
import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.abilities.IAttackAbility;
import bryanthedragon.morph.api.creative.MorphList;
import bryanthedragon.morph.api.creative.sections.UserSection;
import bryanthedragon.morph.api.settings.MorphSettings;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;
import bryanthedragon.morph.core.registry.MorphCoreSection;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Morph manager class
 * 
 * This manager is responsible for managing available morphings.
 */
public class MorphManager
{
    /**
     * Default <s>football</s> morph manager
     */
    public static final MorphManager INSTANCE = new MorphManager();

    /**
     * Registered abilities 
     */
    public Map<String, IAbility> abilities = new HashMap<String, IAbility>();

    /**
     * Registered actions 
     */
    public Map<String, IAction> actions = new HashMap<String, IAction>();

    /**
     * Registered attacks 
     */
    public Map<String, IAttackAbility> attacks = new HashMap<String, IAttackAbility>();

    /**
     * Registered morph factories
     */
    public List<IMorphFactory> factories = new ArrayList<IMorphFactory>();

    /**
     * Active morph settings from JSON config
     */
    public Map<String, MorphSettings> activeSettings = new HashMap<String, MorphSettings>();

    /**
     * Active blacklist. Sent either from server, or getting assigned on 
     * server start. Modifying this is a not a good idea 
     */
    public Set<String> activeBlacklist = new TreeSet<String>();

    /**
     * Active morph ID remapper
     */
    public Map<String, String> activeMap = new HashMap<String, String>();

    /**
     * Global morph list
     */
    public final MorphList list = new MorphList();

    /**
     * Check whether morph by the given name is blacklisted 
     */
    public static boolean isBlacklisted(String name)
    {
        return INSTANCE.activeBlacklist.contains(name);
    }

    /**
     * Set currently used morph ID blacklist
     */
    public void setActiveBlacklist(Level world, Set<String> blacklist)
    {
        this.activeBlacklist.clear();
        this.activeBlacklist.addAll(blacklist);

        MorphCoreSection section = this.list.getSection(MorphCoreSection.class);

        if (section != null && world != null)
        {
            section.reset();
            section.update(world);
        }
    }

    /**
     * Set currently used morph settings
     */
    public void setActiveSettings(Map<String, MorphSettings> settings)
    {
        Map<String, MorphSettings> newSettings = new HashMap<String, MorphSettings>();

        for (Map.Entry<String, MorphSettings> entry : settings.entrySet())
        {
            String key = entry.getKey();
            MorphSettings setting = this.activeSettings.get(key);

            if (setting == null)
            {
                setting = entry.getValue();
            }
            else
            {
                setting.copy(entry.getValue());
            }

            newSettings.put(key, setting);
        }

        this.activeSettings = newSettings;
    }

    /**
     * Set current morph ID remapper
     */
    public void setActiveMap(Map<String, String> map)
    {
        this.activeMap.clear();
        this.activeMap.putAll(map);
    }

    /**
     * That's a singleton, boy!
     */
    private MorphManager()
    {
        
    }

    /**
     * Register all morph factories 
     */
    public void register()
    {
        this.list.register(new UserSection("user"));

        for (int i = this.factories.size() - 1; i >= 0; i--)
        {
            this.factories.get(i).register(this);
        }
    }

    /**
     * Register morph editors 
     */
    @SuppressWarnings("rawtypes")
    @OnlyIn(Dist.CLIENT)
    public void registerMorphEditors(Minecraft mc, List<GuiAbstractMorph> editors)
    {
        for (int i = this.factories.size() - 1; i >= 0; i--)
        {
            this.factories.get(i).registerMorphEditors(mc, editors);
        }
    }

    /**
     * Checks if manager has given morph by ID and NBT tag compound
     * 
     * This meethod iterates over all {@link IMorphFactory}s and if any of them 
     * returns true, then there's a morph, otherwise false.
     */
    public boolean hasMorph(String name)
    {
        name = this.remap(name);

        if (isBlacklisted(name))
        {
            return false;
        }

        for (int i = this.factories.size() - 1; i >= 0; i--)
        {
            if (this.factories.get(i).hasMorph(name))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get an abstract morph from NBT
     * 
     * This method iterates over all {@link IMorphFactory}s, returns a morph 
     * from the first morph factory that does have a morph.
     */
    public AbstractMorph morphFromNBT(CompoundTag tag)
    {
        if (tag == null)
        {
            return null;
        }

        if (tag.contains("Name"))
        {
            tag.putString("Name", this.remap(tag.getString("Name")));
        }

        String name = tag.getString("Name");

        if (isBlacklisted(name))
        {
            return null;
        }

        for (int i = this.factories.size() - 1; i >= 0; i--)
        {
            if (this.factories.get(i).hasMorph(name))
            {
                AbstractMorph morph = this.factories.get(i).getMorphFromNBT(tag);
                
                if (morph != null)
                {
                    this.applySettings(morph);
                    
                    return morph;
                }
            }
        }

        return null;
    }

    /**
     * Apply morph settings on a given morph 
     */
    public void applySettings(AbstractMorph morph)
    {
        morph.setActiveSettings(this.activeSettings.get(morph.name));
    }

    /**
     * Get morph from the entity
     * 
     * Here I should add some kind of mechanism that allows people to substitute 
     * the name of the morph based on the given entity (in the future with 
     * introduction of the public API).
     */
    public String morphNameFromEntity(Entity entity)
    {
        return EntityList.getKey(entity).toString();
    }

    /**
     * Remap morph name
     */
    public String remap(String name)
    {
        String remapped = this.activeMap.get(name);

        return remapped == null ? name : remapped;
    }
}