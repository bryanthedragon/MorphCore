package org.bryanthedragon.morph.api.morphs.main;

import java.util.Objects;
import java.util.UUID;

import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.api.settings.MorphSettings;
import org.bryanthedragon.morph.api.utils.entity.EntityUtils;
import org.bryanthedragon.morph.bodypart.BodyPartManager;
import org.bryanthedragon.morph.bodypart.IBodyPartProvider;
import org.bryanthedragon.morph.entity.SoundHandler;
import org.bryanthedragon.morph.utils.resources.RLUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMorph extends AbstractMorph implements IBodyPartProvider
{
    @OnlyIn(Dist.CLIENT)
    public static LivingEntity renderEntity;

    public BodyPartManager parts = new BodyPartManager();
    protected LivingEntity entity;
    protected CompoundTag entityData;
    protected boolean customSettings;
    protected boolean updatingEntity;
    protected MorphSettings entitySettings;
    public float scale = 1F;
    public ResourceLocation userTexture;

    public EntityMorph(Object object, Level level, UUID uuid, AbstractMorph morph) 
    {
        
    }

    public EntityMorph() 
    {
        
    }

    @Override
    public BodyPartManager getBodyPart()
    {
        return this.parts;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected String getSubclassDisplayName()
    {
        return this.name;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOnScreen(Player player, int x, int y, float scale, float alpha)
    {
        
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderHand(Player player, net.minecraft.world.InteractionHand hand)
    {
        return !this.getSettings().hands;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(LivingEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.getEntity(entity.level());
    }

    protected void setEntitySettings(MorphSettings settings)
    {
        this.entitySettings = settings;
        this.needSettingsUpdate = true;
    }

    @Override
    public void initializeSettings()
    {
        if (!this.needSettingsUpdate)
        {
            return;
        }

        this.settings = MorphSettings.DEFAULT_MORPHED.copy();
        if (this.entitySettings != null)
        {
            this.settings.applyOverrides(this.entitySettings);
        }
        if (this.activeSettings != null)
        {
            this.settings.applyOverrides(this.activeSettings);
        }
        finishInitializingSettings();
    }

    @OnlyIn(Dist.CLIENT)
    protected void setupBodyPart()
    {
    }

    @OnlyIn(Dist.CLIENT)
    public void setupLimbs()
    {
        this.parts.initBodyParts();
    }

    public void setupRenderer()
    {
    }

    public void setupEntity(Level level)
    {
        EntityType.byString(this.name).ifPresent(type -> {
            LivingEntity created = type.create(level) instanceof LivingEntity living ? living : null;
            if (created == null)
            {
                return;
            }

            if (this.entityData != null)
            {
                created.deserializeNBT(this.entityData);
            }
            created.clearFire();
            this.setEntity(created);
        });
    }

    public void setEntity(LivingEntity entity)
    {
        this.entity = entity;
        entity.setHealth(entity.getMaxHealth());
        entity.noPhysics = true;
        entity.setNoGravity(true);

        MorphSettings settings = new MorphSettings();
        settings.health = (int) entity.getMaxHealth();
        settings.hostile = entity instanceof Mob;
        if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null)
        {
            settings.speed = entity instanceof Player ?
                (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED) :
                0.4F * (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        }
        this.setEntitySettings(settings);

        if (this.entityData == null)
        {
            this.entityData = EntityUtils.stripEntityNBT(entity.serializeNBT());
        }
    }

    public LivingEntity getEntity()
    {
        return this.entity;
    }

    public LivingEntity getEntity(Level level)
    {
        if (this.entity == null)
        {
            this.setupEntity(level);
        }
        return this.entity;
    }

    @Override
    public void update(LivingEntity target)
    {
        if (this.entity == null)
        {
            this.setupEntity(target.level());
        }
        if (this.entity == null)
        {
            return;
        }

        this.updatingEntity = true;
        this.entity.setPos(target.getX(), target.getY(), target.getZ());
        this.entity.setYRot(target.getYRot());
        this.entity.setXRot(target.getXRot());
        this.entity.setDeltaMovement(target.getDeltaMovement());
        this.entity.setShiftKeyDown(target.isShiftKeyDown());
        this.entity.setSprinting(target.isSprinting());
        this.entity.setOnGround(target.onGround());
        this.entity.setHealth(target.getHealth());
        this.updatingEntity = false;

        super.update(target);
        this.parts.updateBodyLimbs(this, target);
    }

    protected void updateEntity(LivingEntity target)
    {
        if (this.getSettings().updates && this.entity != null)
        {
            this.entity.setSilent(!MorphCore.showMorphIdleSounds.get());
        }
    }

    public boolean isUpdatingEntity()
    {
        return this.updatingEntity;
    }

    @Override
    protected void updateUserHitbox(LivingEntity target)
    {
        if (this.entity != null)
        {
            this.updateSize(target, this.entity.getBbWidth(), this.entity.getBbHeight());
        }
    }

    public void setEntityData(CompoundTag tag)
    {
        this.entityData = tag;
    }

    public CompoundTag getEntityData()
    {
        return this.entityData;
    }

    public void resetEntity()
    {
        this.entity = null;
        this.entitySettings = null;
        this.needSettingsUpdate = true;
    }

    @Override
    public AbstractMorph create()
    {
        return new EntityMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);
        if (from instanceof EntityMorph morph)
        {
            this.entitySettings = morph.entitySettings == null ? null : morph.entitySettings.copy();
            this.entityData = morph.entityData == null ? null : morph.entityData.copy();
            this.parts.copy(morph.parts);
            this.scale = morph.scale;
            this.userTexture = RLUtils.clone(morph.userTexture);
        }
    }

    @Override
    public float getWidth(LivingEntity target)
    {
        LivingEntity entity = this.getEntity(target.level());
        return entity == null ? 0.6F : entity.getBbWidth();
    }

    @Override
    public float getHeight(LivingEntity target)
    {
        LivingEntity entity = this.getEntity(target.level());
        return entity == null ? 1.8F : entity.getBbHeight();
    }

    @Override
    public SoundEvent getHurtSound(LivingEntity target, DamageSource source)
    {
        return SoundHandler.NO_SOUND;
    }

    @Override
    public SoundEvent getDeathSound(LivingEntity target)
    {
        return SoundHandler.NO_SOUND;
    }

    @Override
    public boolean hasCustomStepSound(LivingEntity target)
    {
        return false;
    }

    @Override
    public void reset()
    {
        this.parts.reset();
        this.resetEntity();
        this.entityData = null;
        this.scale = 1F;
        this.userTexture = null;
        super.reset();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof EntityMorph morph))
        {
            return super.equals(obj);
        }
        return super.equals(obj) && EntityUtils.compareData(morph.entityData, this.entityData) && Objects.equals(morph.parts, this.parts) && morph.scale == this.scale && Objects.equals(morph.userTexture, this.userTexture);
    }

    @Override
    public void toNBT(CompoundTag tag)
    {
        super.toNBT(tag);
        if (this.entityData != null)
        {
            tag.put("EntityData", this.entityData);
        }
        tag.putFloat("Scale", this.scale);
        if (this.userTexture != null)
        {
            tag.put("Texture", RLUtils.writeNbt(this.userTexture));
        }
        ListTag bodyParts = this.parts.toNBT();
        if (bodyParts != null)
        {
            tag.put("BodyParts", bodyParts);
        }
    }

    @Override
    public void fromNBT(CompoundTag tag)
    {
        super.fromNBT(tag);
        this.entityData = tag.getCompound("EntityData");
        if (tag.contains("Scale"))
        {
            this.scale = tag.getFloat("Scale");
        }
        if (tag.contains("Texture"))
        {
            this.userTexture = RLUtils.create(tag.get("Texture"));
        }
        if (tag.contains("BodyParts", 9))
        {
            this.parts.fromNBT(tag.getList("BodyParts", 10));
        }
    }
}
