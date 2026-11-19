package org.bryanthedragon.morph.api.utils.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import org.bryanthedragon.morph.api.models.IMorphProvider;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.api.morphs.main.EntityMorph;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.MorphingProvider;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

import net.minecraftforge.common.util.LazyOptional;

/**
 * Entity utilities methods
 * 
 * Methods that are related to {@link EntityMorph} are going here.
 */
public class EntityUtils
{
    /**
     * List of attributes to remove
     */
    @SuppressWarnings("unused")
    private static List<String> removeAttributes = Lists.newArrayList("generic.followRange");

    /**
     * Strip some common {@link Entity} related tags, so there won't be 
     * interference with comparing two tags on  
     */
    public static CompoundTag stripEntityNBT(CompoundTag tag)
    {
        /* Meta stuff */
        tag.remove("Dimension");
        tag.remove("HurtTime");
        tag.remove("DeathTime");
        tag.remove("HurtByTimestamp");
        tag.remove("Health");
        tag.remove("PortalCooldown");
        tag.remove("Leashed");
        tag.remove("Air");
        tag.remove("id");
        tag.remove("Invulnerable");

        /* Inventory and equipment */
        tag.remove("ArmorDropChances");
        tag.remove("HandDropChances");
        tag.remove("HandItems");
        tag.remove("Inventory");
        tag.remove("LeftHanded");
        tag.remove("CanPickUpLoot");

        /* Space data */
        tag.remove("Pos");
        tag.remove("Motion");
        tag.remove("Rotation");
        tag.remove("FallDistance");
        tag.remove("FallFlying");
        tag.remove("OnGround");
        tag.remove("Fire");
        tag.remove("ArmorItems");

        /* UUID */
        tag.remove("UUIDLeast");
        tag.remove("UUIDMost");

        /* Attributes */
        tag.remove("Attributes");

        /* Shulker tags stripping */
        tag.remove("Peek");
        tag.remove("AttachFace");
        tag.remove("APX");
        tag.remove("APY");
        tag.remove("APZ");

        /* Zombie pigmen stripping */
        tag.remove("Anger");
        tag.remove("HurtBy");
        
        /* Chicken de-egging */
        tag.remove("EggLayTime");

        return tag;
    }

    /**
     * Compare two {@link CompoundTag}s for morphing acquiring
     */
    public static boolean compareData(CompoundTag a, CompoundTag b)
    {
        /* Different count of tags? They're different */
        if (a.size() != b.size())
        {
            return false;
        }

        for (String key : a.getAllKeys())
        {
            Tag aTag = a.get(key);
            Tag bTag = b.get(key);

            /* Supporting condition for size check above, in case if the size 
             * the same, but different keys are missing */
            if (bTag == null)
            {
                return false;
            }

            /* We check only strings and primitives, lists and compounds aren't
             * concern of mine */
            if (!(aTag instanceof NumericTag) && !(aTag instanceof StringTag))
            {
                continue;
            }

            if (!aTag.equals(bTag))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Get slot for given index of {@link Entity#getEquipmentAndArmor()}. I 
     * assume that it would be the same all the time, across all of the 
     * subclasses of {@link Entity}.
     */
    public static EquipmentSlot slotForIndex(int index)
    {
        EquipmentSlot slot = EquipmentSlot.MAINHAND;

        switch (index)
        {
            case 1:
                slot = EquipmentSlot.OFFHAND;
            break;
            case 2:
                slot = EquipmentSlot.FEET;
            break;
            case 3:
                slot = EquipmentSlot.LEGS;
            break;
            case 4:
                slot = EquipmentSlot.CHEST;
            break;
            case 5:
                slot = EquipmentSlot.HEAD;
            break;
        }

        return slot;
    }

    /**
     * Get morph from an entity 
     */
    public static AbstractMorph getMorph(LivingEntity entity)
    {
        if (entity instanceof IMorphProvider)
        {
            return ((IMorphProvider) entity).getMorph();
        }
        else
        {
            LazyOptional<IMorphing> cap = entity.getCapability(MorphingProvider.MORPHING_CAP, null);

            if (cap.isPresent())
            {
                return cap.map(IMorphing::getCurrentMorph).orElse(null);
            }
        }

        return null;
    }

    /**
     * Get string pose for entity based on its attributes or based on given 
     * custom pose
     */
    public static String getPose(LivingEntity entity, String custom, boolean sneak)
    {
        boolean empty = custom.isEmpty();

        if (!empty && !sneak)
        {
            return custom;
        }

        if (entity.isFallFlying())
        {
            return "flying";
        }
        else if (entity.isPassenger())
        {
            return "riding";
        }
        else if (entity.isShiftKeyDown())
        {
            return sneak && !empty ? custom : "sneaking";
        }

        return "standing";
    }

    /**
     * Get the entity at which given player is looking at.
     * Taken from EntityRenderer class.
     *
     * That's a big method... Why Minecraft has lots of these big methods?
     */
    public static Entity getTargetEntity(Entity input, double maxReach)
    {
        double blockDistance = maxReach;

        HitResult result = rayTrace(input, maxReach, 1.0F);
        Vec3 eyes = new Vec3(input.getX(), input.getY() + input.getEyeHeight(), input.getZ());

        if (result != null)
        {
            blockDistance = result.getLocation().distanceTo(eyes);
        }

        Vec3 look = input.getLookAngle();
        Vec3 max = eyes.add(look.scale(maxReach));
        Entity target = null;

        float area = 1.0F;

        List<Entity> list = input.level().getEntities(input, input.getBoundingBox().move(look.scale(maxReach)).inflate(area, area, area), new Predicate<Entity>()
        {
            @Override
            public boolean apply(@Nullable Entity entity)
            {
                return entity != null && entity.canBeCollidedWith();
            }
        });

        double entityDistance = blockDistance;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = list.get(i);

            if (entity == input)
            {
                continue;
            }

            AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
            java.util.Optional<Vec3> interceptOpt = aabb.clip(eyes, max);

            if (aabb.contains(eyes))
            {
                if (entityDistance >= 0.0D)
                {
                    target = entity;
                    entityDistance = 0.0D;
                }
            }
            else if (interceptOpt.isPresent())
            {
                Vec3 intercept = interceptOpt.get();
                double eyesDistance = eyes.distanceTo(intercept);

                if (eyesDistance < entityDistance || entityDistance == 0.0D)
                {
                    if (entity.getRootVehicle() == input.getRootVehicle() && !input.canRiderInteract())
                    {
                        if (entityDistance == 0.0D)
                        {
                            target = entity;
                        }
                    }
                    else
                    {
                        target = entity;
                        entityDistance = eyesDistance;
                    }
                }
            }
        }

        return target;
    }

    /**
     * This method is extracted from {@link Entity} class, because it was marked
     * as client side only code.
     */
    public static HitResult rayTrace(Entity input, double blockReachDistance, float partialTicks)
    {
        Vec3 eyePos = new Vec3(input.getX(), input.getY() + input.getEyeHeight(), input.getZ());
        Vec3 eyeDir = input.getLookAngle();
        Vec3 eyeReach = eyePos.add(eyeDir.scale(blockReachDistance));

        return input.level().clip(new ClipContext(eyePos, eyeReach, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, input));
    }

    public static void forceUpdateSize(Player player, AbstractMorph morph)
    {
        if (morph != null)
        {
            morph.updateSize(player, morph.getWidth(player), morph.getHeight(player));
        }
        else
        {
            float width;
            float height;
            if (player.isFallFlying())
            {
                width = 0.6F;
                height = 0.6F;
            }
            else if (player.isSleeping())
            {
                width = 0.2F;
                height = 0.2F;
            }
            else if (player.isShiftKeyDown())
            {
                width = 0.6F;
                height = 1.65F;
            }
            else
            {
                width = 0.6F;
                height = 1.8F;
            }

            AbstractMorph.updateSizeDefault(player, width, height);
        }
    }

    public static boolean canPlayerMorphFit(Player player, AbstractMorph currentMorph, AbstractMorph newMorph)
    {
        boolean canFit;

        forceUpdateSize(player, newMorph);
        canFit = player.level().noCollision(player, player.getBoundingBox());
        forceUpdateSize(player, currentMorph);

        return canFit;
    }
}