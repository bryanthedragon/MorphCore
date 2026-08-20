package bryanthedragon.morph.api.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.netty.buffer.ByteBuf;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.abilities.IAbility;
import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.abilities.IAttackAbility;
import bryanthedragon.morph.utils.ByteBufUtils;


/**
 * Morph settings
 * 
 * An instance of this class is responsible for storing information about 
 * morph's configurable settings.
 * 
 * For well-defined defaults, use {@link #DEFAULT_MORPHED} or {@link #DEFAULT}.
 */
public class MorphSettings
{
    /**
     * "Safe" settings, equivalent to not being morphed
     */
    public static final MorphSettings DEFAULT = new MorphSettings();
    
    static
    {
        DEFAULT.hostile = false;
        DEFAULT.hands = true;
        DEFAULT.updates = false;
    }

    /**
     * Default settings to fall back on for most morphs.
     */
    public static final MorphSettings DEFAULT_MORPHED = new MorphSettings();

    /**
     * Abilities that are going to be applied on a morph 
     */
    public List<IAbility> abilities = new ArrayList<IAbility>();
    public boolean hasAbilities = true;

    /**
     * Attack that is going to be used on a morph
     */
    public IAttackAbility attack = null;
    public boolean hasAttack = true;

    /**
     * Action that is going to be used on a morph
     */
    public IAction action = null;
    public boolean hasAction = true;

    /**
     * Health units which are going to be applied
     */
    public int health = 20;
    public boolean hasHealth = true;

    /**
     * Speed which is going to be applied 
     */
    public float speed = 0.1F;
    public boolean hasSpeed = true;

    /**
     * Hostile flag which is going to be applied
     */
    public boolean hostile = true;
    public boolean hasHostile = true;

    /**
     * Does client tries render hands for this morph 
     */
    public boolean hands = false;
    public boolean hasHands = true;

    /**
     * Does this morph updates itself 
     */
    public boolean updates = true;
    public boolean hasUpdates = true;

    /**
     * Whether to render this morph in shadow pass
     */
    public int shadowOption = 0;
    public boolean hasShadowOption = true;
    public boolean betterLightsShadow = true;
    public boolean hasbetterLightsShadow = true;
    
    /**
     * Morph settings applier lambda
     */
    public static interface Edit
    {
    	void apply(MorphSettings settings);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MorphSettings)
        {
            MorphSettings settings = (MorphSettings) obj;

            return (this.hasAbilities == settings.hasAbilities && (this.abilities.equals(settings.abilities) || this.hasAbilities == false)) &&
                (this.hasAction == settings.hasAction && (Objects.equals(this.action, settings.action) || this.hasAction == false)) &&
                (this.hasAttack == settings.hasAttack && (Objects.equals(this.attack, settings.attack) || this.hasAttack == false)) &&
                (this.hasHealth == settings.hasHealth && (this.health == settings.health || this.hasHealth == false)) &&
                (this.hasSpeed == settings.hasSpeed && (this.speed == settings.speed || this.hasSpeed == false)) &&
                (this.hasHostile == settings.hasHostile && (this.hostile == settings.hostile || this.hasHostile == false)) &&
                (this.hasUpdates == settings.hasUpdates && (this.updates == settings.updates || this.hasUpdates == false)) &&
                (this.hasShadowOption == settings.hasShadowOption && (this.shadowOption == settings.shadowOption || this.hasShadowOption == false) &&
                (this.hasbetterLightsShadow == settings.hasbetterLightsShadow && (this.betterLightsShadow == settings.betterLightsShadow || this.hasbetterLightsShadow == false)));
        }

        return super.equals(obj);
    }

    public MorphSettings copy()
    {
        MorphSettings settings = new MorphSettings();

        settings.copy(this);

        return settings;
    }

    /**
     * Merge given morph settings with this settings
     */
    public void copy(MorphSettings setting)
    {
        this.abilities.clear();
        this.abilities.addAll(setting.abilities);
        this.hasAbilities = setting.hasAbilities;

        this.action = setting.action;
        this.hasAction = setting.hasAction;
        this.attack = setting.attack;
        this.hasAttack = setting.hasAttack;

        this.health = setting.health;
        this.hasHealth = setting.hasHealth;
        this.speed = setting.speed;
        this.hasSpeed = setting.hasSpeed;
        this.hostile = setting.hostile;
        this.hasHostile = setting.hasHostile;
        this.hands = setting.hands;
        this.hasHands = setting.hasHands;
        this.updates = setting.updates;
        this.hasUpdates = setting.hasUpdates;
        
		this.shadowOption = setting.shadowOption;
		this.hasShadowOption = setting.hasShadowOption;

        this.betterLightsShadow = setting.betterLightsShadow;
        this.hasbetterLightsShadow = setting.hasbetterLightsShadow;
    }

    /**
     * Apply any additional settings to this one as long as they are not null/empty
     */
    public void applyOverrides(MorphSettings setting)
    {
        if (setting.hasAbilities)
        {
            this.abilities.clear();
            this.abilities.addAll(setting.abilities);
            this.hasAbilities = true;
        }

        if (setting.hasAction)
        {
            this.action = setting.action;
            this.hasAction = true;
        }

        if (setting.hasAttack)
        {
            this.attack = setting.attack;
            this.hasAttack = true;
        }

        if (setting.hasHealth)
        {
            this.health = setting.health;
            this.hasHealth = true;
        }

        if (setting.hasSpeed)
        {
            this.speed = setting.speed;
            this.hasSpeed = true;
        }

        if (setting.hasHostile)
        {
            this.hostile = setting.hostile;
            this.hasHostile = true;
        }

        if (setting.hasHands)
        {
            this.hands = setting.hands;
            this.hasHands = true;
        }

        if (setting.hasUpdates)
        {
            this.updates = setting.updates;
            this.hasUpdates = true;
        }

        if (setting.hasShadowOption)
        {
            this.shadowOption = setting.shadowOption;
            this.hasShadowOption = true;
        }

        if (setting.hasbetterLightsShadow)
        {
            this.betterLightsShadow = setting.betterLightsShadow;
            this.hasbetterLightsShadow = true;
        }
    }

    /**
     * Write morph settings to the network buffer
     */
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.hasAbilities);

        if (this.hasAbilities)
        {
    		buf.writeInt(this.abilities.size());
    
            for (IAbility ability : this.abilities)
            {
                String string = getKey(MorphManager.INSTANCE.abilities, ability);
    
                ByteBufUtils.writeUTF8String(buf, string == null ? "" : string);
            }
        }

        buf.writeBoolean(this.hasAction);

        if (this.hasAction)
        {
            String action = getKey(MorphManager.INSTANCE.actions, this.action);
            buf.writeBoolean(action != null);

            if (action != null)
            {
                ByteBufUtils.writeUTF8String(buf, action);
            }
        }

        buf.writeBoolean(this.hasAttack);

        if (this.hasAttack) {
            String attack = getKey(MorphManager.INSTANCE.attacks, this.attack);
            buf.writeBoolean(attack != null);

            if (attack != null)
            {
                ByteBufUtils.writeUTF8String(buf, attack);
            }
        }
        
        buf.writeBoolean(this.hasHealth);

        if (this.hasHealth)
        {
            buf.writeInt(this.health);
        }

        buf.writeBoolean(this.hasSpeed);

        if (this.hasSpeed)
        {
            buf.writeFloat(this.speed);
        }

        buf.writeBoolean(this.hasHostile);

        if (this.hasHostile)
        {
            buf.writeBoolean(this.hostile);
        }

        buf.writeBoolean(this.hasHands);

        if (this.hasHands)
        {
            buf.writeBoolean(this.hands);
        }

        buf.writeBoolean(this.hasUpdates);

        if (this.hasUpdates)
        {
            buf.writeBoolean(this.updates);
        }

        buf.writeBoolean(this.hasShadowOption);

        if (this.hasShadowOption)
        {
        	buf.writeInt(this.shadowOption);
        }

        buf.writeBoolean(this.hasbetterLightsShadow);

        if (this.hasbetterLightsShadow)
        {
            buf.writeBoolean(this.betterLightsShadow);
        }
    }

    /**
     * Read morph settings from the network buffer 
     */
    public void fromBytes(ByteBuf buf)
    {
        this.hasAbilities = buf.readBoolean();

        if (this.hasAbilities)
        {
            List<IAbility> abilities = new ArrayList<IAbility>();
            for (int i = 0, c = buf.readInt(); i < c; i++)
            {
                IAbility ability = MorphManager.INSTANCE.abilities.get(ByteBufUtils.readUTF8String(buf));
    
                if (ability != null)
                {
                    abilities.add(ability);
                }
            }

            this.abilities = abilities;
        }

        this.hasAction = buf.readBoolean();

        if (this.hasAction)
        {
            if (buf.readBoolean())
            {
                String action = ByteBufUtils.readUTF8String(buf);
                this.action = MorphManager.INSTANCE.actions.get(action);
            }
            else
            {
                this.action = null;
            }
        }

        this.hasAttack = buf.readBoolean();

        if (this.hasAttack)
        {
            if (buf.readBoolean())
            {
                String attack = ByteBufUtils.readUTF8String(buf);
                this.attack = MorphManager.INSTANCE.attacks.get(attack);
            }
            else
            {
                this.attack = null;
            }
        }
        
        this.hasHealth = buf.readBoolean();

        if (this.hasHealth)
        {
        	this.health = buf.readInt();
        }
        
        this.hasSpeed = buf.readBoolean();

        if (this.hasSpeed)
        {
        	this.speed = buf.readFloat();
        }
        
        this.hasHostile = buf.readBoolean();

        if (this.hasHostile)
        {
        	this.hostile = buf.readBoolean();
        }
        
        this.hasHands = buf.readBoolean();

        if (this.hasHands)
        {
        	this.hands = buf.readBoolean();
        }
        
        this.hasUpdates = buf.readBoolean();

        if (this.hasUpdates)
        {
        	this.updates = buf.readBoolean();
        }
        
        this.hasShadowOption = buf.readBoolean();

        if (this.hasShadowOption)
        {
        	this.shadowOption = buf.readInt();
        }

        this.hasbetterLightsShadow = buf.readBoolean();

        if (this.hasbetterLightsShadow)
        {
            this.betterLightsShadow = buf.readBoolean();
        }
    }

    /**
     * Save properties to NBT compound
     */
    public void toNBT(CompoundTag tag)
    {
    	if (this.hasAbilities)
        {
            ListTag list = new ListTag();

            for (IAbility ability : this.abilities)
            {
                list.add(StringTag.valueOf(getKey(MorphManager.INSTANCE.abilities, ability)));
            }

            tag.put("Abilities", list);
        }

        if (this.hasAttack)
        {
            String attackKey = getKey(MorphManager.INSTANCE.attacks, this.attack);
            if (attackKey == null) { attackKey = "null"; }
        	tag.putString("Attack", attackKey);
        }

        if (this.hasAction)
        {
            String actionKey = getKey(MorphManager.INSTANCE.actions, this.action);

            if (actionKey == null) { actionKey = "null"; }

        	tag.putString("Action", actionKey);
        }

        if (this.hasHealth)
        {
            tag.putInt("HP", this.health);
        }

        if (this.hasSpeed)
        {
            tag.putFloat("Speed", this.speed);
        }

        if (this.hasHostile)
        {
            tag.putBoolean("Hostile", this.hostile);
        }

        if (this.hasHands)
        {
            tag.putBoolean("Hands", this.hands);
        }

        if (this.hasUpdates)
        {
            tag.putBoolean("Updates", this.updates);
        }

        if (this.hasShadowOption)
        {
            tag.putInt("ShadowOption", this.shadowOption);
        }

        if (this.hasbetterLightsShadow)
        {
            tag.putBoolean("BetterLightsShadow", this.betterLightsShadow);
        }
    }

    /**
     * Read properties from NBT compound
     */
    public void fromNBT(CompoundTag tag)
    {
        this.hasAbilities = tag.contains("Abilities");

    	if (this.hasAbilities)
        {
        	ListTag list = tag.getList("Abilities", 8);

            this.abilities.clear();

            for (int i = 0; i < list.size(); i ++)
            {
                IAbility ability = MorphManager.INSTANCE.abilities.get(list.getString(i));

                if (ability != null)
                {
                    this.abilities.add(ability);
                }
            }
        }

    	this.hasAttack = tag.contains("Attack");

        if (this.hasAttack)
        {
            this.attack = MorphManager.INSTANCE.attacks.get(tag.getString("Attack"));
        }

        this.hasAction = tag.contains("Action");

        if (this.hasAction)
        {
            this.action = MorphManager.INSTANCE.actions.get(tag.getString("Action"));
        }

        this.hasHealth = tag.contains("HP");

        if (this.hasHealth)
        {
            this.health = tag.getInt("HP");
        }

        this.hasSpeed = tag.contains("Speed");

        if (this.hasSpeed)
        {
            this.speed = tag.getFloat("Speed");
        }

        this.hasHostile = tag.contains("Hostile");

        if (this.hasHostile)
        {
            this.hostile = tag.getBoolean("Hostile");
        }

        this.hasHands = tag.contains("Hands");

        if (this.hasHands)
        {
            this.hands = tag.getBoolean("Hands");
        }

        this.hasUpdates = tag.contains("Updates");

        if (this.hasUpdates)
        {
            this.updates = tag.getBoolean("Updates");
        }

        this.hasShadowOption = tag.contains("ShadowOption");

        if (this.hasShadowOption)
        {
            this.shadowOption = tag.getInt("ShadowOption");
        }

        this.hasbetterLightsShadow = tag.contains("BetterLightsShadowOption");

        if (this.hasbetterLightsShadow)
        {
            this.betterLightsShadow = tag.getBoolean("BetterLightsShadow");
        }
    }

    /**
     * Get key of given value in given map 
     */
    public static <T> String getKey(Map<String, T> map, T value)
    {
        if (value == null)
        {
            return null;
        }

        for (Map.Entry<String, T> entry : map.entrySet())
        {
            if (entry.getValue() == value)
            {
                return entry.getKey();
            }
        }

        return null;
    }
}
