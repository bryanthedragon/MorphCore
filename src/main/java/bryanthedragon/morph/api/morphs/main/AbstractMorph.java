package bryanthedragon.morph.api.morphs.main;

import java.util.Objects;

import org.joml.Vector3f;

import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.settings.MorphSettings;
import bryanthedragon.morph.api.abilities.IAbility;
import bryanthedragon.morph.api.morphs.utils.Hitbox;
import bryanthedragon.morph.entity.SoundHandler;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


/**
 * Base class for all different types of morphs
 * 
 * This is an abstract morph. It contains all needed properties for a basic 
 * morph such as abilities, action, attack, health, speed and hotstyle flag.
 * 
 * This class is also responsible for rendering operations. Oh boy, this class 
 * is so huge. I'll have to decompose this thing onto rendering and logic code.
 */
public abstract class AbstractMorph
{
    public Vector3f cachedTranslation = new Vector3f();

    /**
     * At the moment no use -> would be useful to have realistic physics
     * for every limb, see robotics https://www.tu-chemnitz.de/informatik/KI/edu/robotik/ws2017/vel.kin.pdf
     */
    public Vector3f angularVelocity = new Vector3f();

    public int age = 0;

    /* Meta information */

    /**
     * Morph's name
     */
    public String name = "";

    /**
     * Morph's display name
     */
    public String displayName = "";

    /* Survival morph properties */

    /**
     * Is this morph is favorite 
     */
    public boolean favorite = false;

    /**
     * Is this morph is favorite
     */
    public int keybind = -1;

    /* Morph Settings */

    /**
     * The authoritative settings for the morph.
     */
    public MorphSettings settings = MorphSettings.DEFAULT.copy();

    /**
     * If this is false, {@link #settings} will be initialized
     * as needed. If this is true, {@link #settings} will remain
     * at whatever value it was set to in {@link #forceSettings(MorphSettings)}
     */
    protected boolean forcedSettings = false;
    
    protected boolean needSettingsUpdate = false;

    /**
     * The highest priority settings, defined through
     * configuration.
     */
    protected MorphSettings activeSettings = null;
    
    /**
     * This is called to initialize settings for morphs, if
     * settings are out-of-date.
     */
    public void initializeSettings()
    {
        if (!this.needSettingsUpdate)
        {
        	if (this.settings == null)
        	{
                MorphCore.LOGGER.error("needSettingsUpdate was not set to true when changing morph settings, or the settings was set to null directly when it shouldn't be");
                this.settings = MorphSettings.DEFAULT_MORPHED.copy();
        	}
        	
            return;
        }

        this.settings = MorphSettings.DEFAULT_MORPHED.copy();

        if (this.activeSettings != null)
        {
            this.settings.applyOverrides(this.activeSettings);
        }
        
        finishInitializingSettings();
    }
    
    protected void finishInitializingSettings()
    {
        this.needSettingsUpdate = false;
        this.forcedSettings = false;
    }

    /**
     * This sets the active settings for the morph, usually defined
     * by the user through JSON configuration. These settings usually
     * have the highest priority.
     */
    public void setActiveSettings(MorphSettings activeSettings)
    {
        this.activeSettings = activeSettings;
        this.needSettingsUpdate = true;
    }
    
    /**
     * This forces a morph to use the given settings. These settings
     * will not be overridden and must be complete settings
     * (no null fields allowed).
     */
    public void forceSettings(MorphSettings default1)
    {
    	if (default1 == null)
    	{
        	default1 = MorphSettings.DEFAULT_MORPHED.copy();
    	}
    	
        this.settings = default1;
        this.forcedSettings = true;
    }

    /**
     * This forces a morph to use the updated settings. These settings
     * will not be overridden and must be complete settings
     * (no null fields allowed).
     * If the settings have not been forced yet, the morph's existing
     * settings will be copied and used as the basis for the new forced
     * settings.
     */
    public void forceEditSettings(MorphSettings.Edit edit)
    {
    	MorphSettings settingsCopy = this.getSettings().copy();
    	edit.apply(settingsCopy);
    	forceSettings(settingsCopy);
    }
    
    /**
     * Undoes the effects of {@link #forceSettings}
     */
    public void clearForcedSettings()
    {
        this.settings = null;
        this.forcedSettings = false;
        this.needSettingsUpdate = true;
    }
    
    /**
     * Gets the morph settings or initializes them if
     * not defined.
     */
    public MorphSettings getSettings()
    {
        if (!this.forcedSettings)
        {
            initializeSettings();
        }

        return this.settings;
    }

    /**
     * Custom hitbox setting
     */
    public Hitbox hitbox = new Hitbox();

    /**
     * Whether this morph is erroring when rendering
     */
    public boolean errorRendering;

    /**
     * Get display name of this morph
     */
    @OnlyIn(Dist.CLIENT)
    public String getDisplayName()
    {
        if (this.displayName != null && !this.displayName.isEmpty())
        {
            return this.displayName;
        }

        return this.getSubclassDisplayName();
    }

    @OnlyIn(Dist.CLIENT)
    protected String getSubclassDisplayName()
    {
        return this.name;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasCustomName()
    {
        return this.displayName != null && !this.displayName.isEmpty();
    }

    public boolean hasCustomSettings()
    {
    	if (this.settings != MorphSettings.DEFAULT)
    	{
    		return true;
    	}
    	
    	if (this.settings != MorphManager.INSTANCE.activeSettings.get(this.name))
    	{
    		return true;
    	}
    	
        return forcedSettings;
    }

    /* Render methods */

    /**
     * Render this morph on 2D screen (used in GUIs)
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void renderOnScreen(Player player, int x, int y, float scale, float alpha);

    /**
     * Render the entity (in the world) 
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void render(LivingEntity entity, double x, double y, double z, float entityYaw, float partialTicks);

    /**
     * Render the arm for given hand 
     */
    @OnlyIn(Dist.CLIENT)
    public boolean renderHand(Player player, net.minecraft.world.InteractionHand hand)
    {
        return !getSettings().hands;
    }

    /* Update loop */

    /**
     * Update the player based on its morph abilities and properties. This 
     * method also responsible for updating AABB size. 
     */
    public void update(LivingEntity target)
    {
        this.updateHitbox(target);

        MorphSettings settings = this.getSettings();

        if (settings.speed != 0.1F)
        {
            target.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(settings.speed);
        }

        for (IAbility ability : settings.abilities)
        {
            ability.update(target);
        }

        this.age++;
    }

    /* Morph and demorph handlers */

    /**
     * Morph into the current morph
     * 
     * This method responsible for setting up the health of the player to 
     * morph's health and invoke ability's onMorph methods.
     */
    public void morph(LivingEntity target)
    {
        for (IAbility ability : this.getSettings().abilities)
        {
            ability.onMorph(target);
        }
    }

    /**
     * Demorph from the current morph
     * 
     * This method responsible for setting up the health back to player's 
     * default health and invoke ability's onDemorph methods.
     */
    public void demorph(LivingEntity target)
    {
        for (IAbility ability : this.getSettings().abilities)
        {
            ability.onDemorph(target);
        }
    }

    /* Adjusting size */

    protected void updateHitbox(LivingEntity target)
    {
        if (this.hitbox.enabled)
        {
            float height = target.isShiftKeyDown() ? this.hitbox.sneakingHeight : this.hitbox.height;

            this.updateSize(target, this.hitbox.width, height, this.hitbox.eye);
        }
        else
        {
            this.updateUserHitbox(target);
        }
    }

    protected void updateUserHitbox(LivingEntity target)
    {}

    public void updateSize(LivingEntity target, float width, float height)
    {
        updateSizeDefault(target, width, height);
    }

    /**
     * Update player's size based on given width and height
     */
    public void updateSize(LivingEntity target, float width, float height, float eyeFactor)
    {
        updateSizeDefault(target, width, height, eyeFactor);
    }

    public static void updateSizeDefault(LivingEntity target, float width, float height)
    {
        updateSizeDefault(target, width, height, 0.9F);
    }

    public static void updateSizeDefault(LivingEntity target, float width, float height, float eyeFactor)
    {
        /* Any lower than this, and the morph will take damage when hitting the ceiling.
         * Likewise, an eye height less than this will cause suffocation damage when standing
         * on the ground.
         * This is hard-coded in vanilla.
         */
        float minEyeToHeadDifference = 0.1F;
        height = Math.max(height, minEyeToHeadDifference * 2);

        if (target instanceof Player && !MorphCore.disablePov.get())
        {
            float eyeHeight = height * eyeFactor;
            if (eyeHeight + minEyeToHeadDifference > height)
            {
                eyeHeight = height - minEyeToHeadDifference;
            }
            ((Player) target).setEyeHeight(eyeHeight);
        }

        /* This is a total rip-off of Player#setSize method */
        if (width != target.getBbWidth() || height != target.getBbHeight())
        {
            AABB aabb = target.getBoundingBox();

            target.setDimensions(net.minecraft.world.entity.EntityDimensions.scalable(width, height));
            target.setBoundingBox(new AABB(target.getX() - width / 2, aabb.minY, target.getZ() - width / 2, target.getX() + width / 2, aabb.minY + height, target.getZ() + width / 2));
        }
    }

    /* Safe shortcuts for activating action and attack */

    /**
     * Execute action with (or on) given player 
     */
    public void action(LivingEntity target)
    {
        if (this.getSettings().action != null)
        {
            this.getSettings().action.execute(target, this);
        }
    }

    /**
     * Attack a target 
     */
    public void attack(net.minecraft.world.entity.Entity target, LivingEntity source)
    {
        if (this.getSettings().attack != null)
        {
            this.getSettings().attack.attack(target, source);
        }
    }

    /**
     * <p>Used when copying morphs</p>
     * 
     * <p>
     * <b>IMPORTANT</b>: When you subclass other morphs, don't forget to override
     * their method with your own.
     * </p>
     */
    public abstract AbstractMorph create();
    
    /**
     * Clone a morph
     */
    public final AbstractMorph copy()
    {
        AbstractMorph morph = this.create();
        // If this fails, then modder forgot to override copy()
        assert(this.getClass().isInstance(morph));

        morph.copy(this);

        return morph;
    }

    /**
     * <p>Copy this {@link AbstractMorph}</p>
     * 
     * <p>
     * <b>IMPORTANT</b>: If you subclass other morphs, and your morph contains new
     * data, don't for get to override their method with your own.
     * </p>
     */
    public void copy(AbstractMorph from)
    {
        this.name = from.name;
        this.displayName = from.displayName;
        this.favorite = from.favorite;
        this.settings = from.settings != null ? from.settings.copy() : null;
        this.activeSettings = from.activeSettings != null ? from.activeSettings.copy() : null;
        this.forcedSettings = from.forcedSettings;
        this.needSettingsUpdate = from.needSettingsUpdate;
        this.keybind = from.keybind;
        this.hitbox.copy(from.hitbox);
    }

    /* Getting size */

    /**
     * Get width of this morph 
     */
    public abstract float getWidth(LivingEntity target);

    /**
     * Get height of this morph 
     */
    public abstract float getHeight(LivingEntity target);

    /**
     * Get the eye height of this morph.
     * Not used by updateSize.
     */
    public float getEyeHeight(LivingEntity target)
    {
        if (!MorphCore.disablePov.get())
        {
            return this.getHeight(target) * 0.9F;
        }
        else
        {
            return 1.62F;
        }
    }

    /**
     * Get the default sound that this morph makes when it
     * is hurt
     */
    public final SoundEvent getHurtSound(LivingEntity target)
    {
        return getHurtSound(target, SoundHandler.GENERIC_DAMAGE);
    }

    /**
     * Get the sound that this morph makes when it
     * is hurt by the given DamageSource, or return null
     * for no change.
     */
    public SoundEvent getHurtSound(LivingEntity target, DamageSource damageSource)
    {
        return null;
    }

    /**
     * Get the sound that this morph makes when it
     * is killed, or return null for no change.
     */
    public SoundEvent getDeathSound(LivingEntity target)
    {
        return null;
    }

    /**
     * Make this return true if you override playStepSound(..)
     */
    public boolean hasCustomStepSound(LivingEntity target)
    {
        return false;
    }

    /**
     * Plays the sound that this morph makes when it
     * takes a step, but only if hasCustomStepSound(..) returns true
     */
    public void playStepSound(LivingEntity target)
    {}

    /**
     * Called when the player just changed dimensions
     */
    public void onChangeDimension(Player player, int oldDim, int currentDim)
    {}

    /**
     * Check either if given object is the same as this morph 
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof AbstractMorph)
        {
            AbstractMorph morph = (AbstractMorph) obj;

            return Objects.equals(this.name, morph.name) &&
                Objects.equals(this.displayName, morph.displayName) &&
                Objects.equals(this.hitbox, morph.hitbox) &&
                Objects.equals(this.forcedSettings ? this.settings : null, morph.forcedSettings ? morph.settings : null);
        }

        return super.equals(obj);
    }

    /**
     * Check whether the morph can be merged (this should allow 
     * overwriting of a morph instead of completely replacing it)
     */
    public boolean canMerge(AbstractMorph morph)
    {
        return false;
    }

    /**
     * Collect the data from previous morph
     */
    public void afterMerge(AbstractMorph morph)
    {}

    /**
     * Whether to activate the use target option in GUIs like BodyPart GUI by default
     * @return false by default
     */
    public boolean useTargetDefault()
    {
        return false;
    }

    /**
     * This method should be used by any morphs that support merging to copy essential
     * whenever they merge
     */
    protected void mergeBasic(AbstractMorph morph)
    {
        this.displayName = morph.displayName;
        this.settings = morph.settings;
        this.forcedSettings = morph.forcedSettings;
        this.hitbox.copy(morph.hitbox);
    }

    /**
     * Reset data for editing 
     */
    public void reset()
    {
        setActiveSettings(null);
        clearForcedSettings();
        this.hitbox.reset();
    }

    /* Reading / writing to NBT */

    public final CompoundTag toNBT()
    {
        CompoundTag tag = new CompoundTag();

        this.toNBT(tag);

        return tag;
    }

    /**
     * Save abstract morph's properties to NBT compound 
     */
    public void toNBT(CompoundTag tag)
    {
        tag.putString("Name", this.name);

        if (this.forcedSettings)
        {
            CompoundTag settings = new CompoundTag();

            this.getSettings().toNBT(settings);

            if (!settings.isEmpty())
            {
                tag.put("Settings", settings);
            }
        }
        
    	tag.putBoolean("ForcedSettings", this.forcedSettings);

        if (this.displayName != null && !this.displayName.isEmpty())
        {
            tag.putString("DisplayName", this.displayName);
        }

        if (this.favorite)
        {
            tag.putBoolean("Favorite", this.favorite);
        }

        if (this.keybind >= 0)
        {
            tag.putInt("Keybind", this.keybind);
        }

        if (!this.hitbox.isDefault())
        {
            tag.put("Hitbox", this.hitbox.toNBT());
        }
    }

    /**
     * Read abstract morph's properties from NBT compound 
     */
    public void fromNBT(CompoundTag tag)
    {
        this.reset();

        this.name = tag.getString("Name");
        
        boolean hasForcedSettings = false;
        if (tag.contains("ForcedSettings"))
        {
        	hasForcedSettings = tag.getBoolean("ForcedSettings");
        }

        if (tag.contains("Settings"))
        {
            this.settings = new MorphSettings();
            this.settings.fromNBT(tag.getCompound("Settings"));
            
            if (hasForcedSettings)
            {
            	this.forcedSettings = true;
            }
        }

        if (tag.contains("DisplayName"))
        {
            this.displayName = tag.getString("DisplayName");
        }           

        if (tag.contains("Favorite"))
        {
            this.favorite = tag.getBoolean("Favorite");
        }

        if (tag.contains("Keybind"))
        {
            this.keybind = tag.getInt("Keybind");
        }

        if (tag.contains("Hitbox"))
        {
            this.hitbox.fromNBT(tag.getCompound("Hitbox"));
        }
    }
}
