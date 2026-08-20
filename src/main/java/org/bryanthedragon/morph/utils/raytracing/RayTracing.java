package bryanthedragon.morph.utils.raytracing;

import java.util.List;
import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayTracing
{
    /**
     * Get the entity at which given player is looking at
     */
    public static Entity getTargetEntity(Entity input, double maxReach)
    {
        HitResult result = rayTraceWithEntity(input, maxReach);

        return result instanceof EntityHitResult ? ((EntityHitResult) result).getEntity() : null;
    }

    /**
     * Kind of like rayTrace method, but as well it takes into account entity
     * ray tracing
     */
    public static HitResult rayTraceWithEntity(Entity input, double maxReach)
    {
        HitResult result = rayTrace(input, maxReach, 1.0F);
        Vec3 eyes = input.getEyePosition(1.0F);
        double entityDistance = result == null ? maxReach : result.getLocation().distanceTo(eyes);

        Vec3 look = input.getViewVector(1.0F);
        Vec3 end = eyes.add(look.scale(maxReach));
        Vec3 hitLocation = null;
        Entity target = null;
        float area = 1.0F;

        List<Entity> entities = input.level().getEntities(input,
            input.getBoundingBox().expandTowards(look.scale(maxReach)).inflate(area),
            entity -> entity.canBeCollidedWith());

        for (Entity entity : entities)
        {
            if (entity == input)
            {
                continue;
            }

            AABB boundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> intercept = boundingBox.clip(eyes, end);

            if (boundingBox.contains(eyes))
            {
                if (entityDistance >= 0.0D)
                {
                    hitLocation = intercept.orElse(eyes);
                    target = entity;
                    entityDistance = 0.0D;
                }
            }
            else if (intercept.isPresent())
            {
                Vec3 intersection = intercept.get();
                double distance = eyes.distanceTo(intersection);

                if (distance < entityDistance || entityDistance == 0.0D)
                {
                    if (entity.getRootVehicle() == input.getRootVehicle() && !input.canRiderInteract())
                    {
                        if (entityDistance == 0.0D)
                        {
                            hitLocation = intersection;
                            target = entity;
                        }
                    }
                    else
                    {
                        hitLocation = intersection;
                        target = entity;
                        entityDistance = distance;
                    }
                }
            }
        }

        return target == null ? result : new EntityHitResult(target, hitLocation);
    }

    /**
     * This method is extracted from {@link Entity} class, because it was marked
     * as client side only code.
     */
    public static HitResult rayTrace(Entity input, double blockReachDistance, float partialTicks)
    {
        Vec3 eyePosition = input.getEyePosition(partialTicks);
        Vec3 eyeReach = eyePosition.add(input.getViewVector(partialTicks).scale(blockReachDistance));

        return input.level().clip(new ClipContext(eyePosition, eyeReach, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, input));
    }
}
