package bryanthedragon.morph.api;

import bryanthedragon.morph.ClientProxy;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.events.SpawnGhostEvent;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.entity.EntityMorph;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Server event handler
 * 
 * This event handler (or rather listener) is responsible for morphings. In 
 * essence, there are few things going on over here:
 * 
 * 1. Update morphs in the player's loop
 * 2. Acquiring morphs from killed entities
 * 3. Grant additional attack effect while morphed (more damage, explosions, 
 *    potion effects, etc.)
 * 4. Cancel attack targeting for hostile morphs 
 */
public class MorphHandler
{
    /* Next tick tasks (used for "knockback" attack) */
    public static List<Runnable> FUTURE_TASKS_CLIENT = new ArrayList<Runnable>();
    public static List<Runnable> FUTURE_TASKS_SERVER = new ArrayList<Runnable>();

    /**
     * When player is morphed, its morphing abilities are executed over here.
     * 
     * Stuff like gliding, allergies, climbing, swimming and other stuff are 
     * get applied on the player over here.
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            return;
        }

        Player player = event.player;
        IMorphing capability = Morphing.get(player);

        this.runFutureTasks(player);

        // A sanity check to prevent "healing" health when morphing to and from
        // a mob
        // with essentially zero health
        // We have to do it every tick because you never know when another mod
        // could
        // change the max health
        if (capability != null)
        {
            // If the current health ratio makes sense, store that ratio in the
            // capability
            float maxHealth = player.getMaxHealth();

            if (maxHealth > IMorphing.REASONABLE_HEALTH_VALUE)
            {
                float healthRatio = player.getHealth() / maxHealth;
                capability.setLastHealthRatio(healthRatio);
            }
        }

        if (capability == null || !capability.isMorphed())
        {
            /* Restore default eye height */
            if (!MorphCore.disablePov.get())
            {
                player.getEyeHeight();
            }
        }

        /* Keep client gui state up-to-date for morphs with the
         * Swim ability.
         */
        if (player.level().isRemote)
        {
            boolean hasSquidAir = false;
            int squidAir = 300;

            if (capability != null)
            {
                hasSquidAir = capability.getHasSquidAir();
                squidAir = capability.getSquidAir();
            }

            ClientProxy.hud.renderSquidAir = hasSquidAir;
            ClientProxy.hud.squidAir = squidAir;
        }

        try
        {
            capability.update(player);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            if (!player.world.isRemote)
            {
                MorphAPI.demorph(player);
            }
        }
    }

    /**
     * When a player kills an entity, he gains a morph based on the properties 
     * of this entity.
     * 
     * I think I need to implement some mechanism or a callback to map some 
     * entities with the same name onto different morphs. 
     */
    @SubscribeEvent
    public void onPlayerKillEntity(LivingDeathEvent event)
    {
        Entity source = event.getSource().getTrueSource();
        Entity target = event.getEntity();

        if (target.world.isRemote || source instanceof FakePlayer)
        {
            return;
        }

        if (!(source instanceof Player) || target instanceof Player || MorphCore.preventKillAcquire.get())
        {
            return;
        }

        Player player = (Player) source;
        IMorphing capability = Morphing.get(player);

        if (capability == null)
        {
            return;
        }

        String name = MorphManager.INSTANCE.morphNameFromEntity(target);

        if (!MorphManager.INSTANCE.hasMorph(name))
        {
            MorphCore.log("Morph by key '" + name + "' doesn't exist!");

            return;
        }

        CompoundTag tag = new CompoundTag();

        tag.setString("Name", name);
        tag.setTag("EntityData", EntityUtils.stripEntityNBT(target.serializeNBT()));

        AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(tag);
        boolean acquired = capability.acquiredMorph(morph);

        if (MorphCore.acquireImmediately.get() && !acquired)
        {
            MorphAPI.acquire(player, morph);

            return;
        }

        if (!MorphCore.preventGhosts.get() || !acquired)
        {
            SpawnGhostEvent spawnGhostEvent = new SpawnGhostEvent.Pre(player, morph);

            if (MinecraftForge.EVENT_BUS.post(spawnGhostEvent) || spawnGhostEvent.morph == null)
            {
                return;
            }
            morph = spawnGhostEvent.morph;

            EntityMorph morphEntity = new EntityMorph(player.level(), player.getUUID(), morph);

            morphEntity.setPositionAndRotation(target.posX, target.posY + target.height / 2, target.posZ, target.rotationYaw, target.rotationPitch);
            player.world.spawnEntity(morphEntity);

            MinecraftForge.EVENT_BUS.post(new SpawnGhostEvent.Post(player, morph));
        }
    }
    
    /**
     * When an EntityMorph is updated, the entity may attempt to hurt the player.
     * This should not be allowed.
     */
    @SubscribeEvent
    public void onMorphAttackPlayer(LivingAttackEvent event)
    {
        Entity target = event.getEntity();
        
        if (target instanceof Player)
        {
            Player player = (Player)target;
            IMorphing capability = Morphing.get(player);
            
            if (capability == null || !capability.isMorphed())
            {
                return;
            }
            
            AbstractMorph morph = capability.getCurrentMorph();
            
            if (morph == null || !(morph instanceof bryanthedragon.morph.api.morphs.EntityMorph))
            {
                return;
            }
            
            bryanthedragon.morph.api.morphs.EntityMorph entityMorph = (bryanthedragon.morph.api.morphs.EntityMorph)morph;
            
            if (entityMorph.isUpdatingEntity())
            {
                // Unfortunately, entities sometimes do damage to the player without telling the player where the damage came from
                // Luckily, entities are ticked in sequence, so we know for certain the player's morph entity is responsible for this
                event.setCanceled(true);
            }
        }
    }

    /**
     * When player is morphed, he can deal an damage or effect onto the enemy. 
     * 
     * For example, if player is morphed into the wither skeleton, he also 
     * grants a target wither potion effect. Pretty realistic, however I don't 
     * really know what that does. 
     */
    @SubscribeEvent
    public void onPlayerAttack(LivingAttackEvent event)
    {
        DamageSource source = event.getSource();
        Entity trueSource = source.getTrueSource();
        Entity target = event.getEntity();

        if(source instanceof EntityDamageSourceIndirect)
        {
            return;
        }
        
        if (trueSource instanceof Player)
        {
            Player player = (Player) trueSource;
            IMorphing capability = Morphing.get(player);

            if (capability == null || !capability.isMorphed())
            {
                return;
            }

            capability.getCurrentMorph().attack(target, player);
        }
    }

    /**
     * Another morphing handler.
     * 
     * This handler is responsible for canceling setting attack target for 
     * hostile morphs.  Also handles any instance where the morph entity
     * is targeted instead of the player, and shifts the targeting onto
     * the player.
     */
    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
    {
        if (MorphCore.disableMorphDisguise.get())
        {
            return;
        }

        Entity target = event.getTarget();
        LivingEntity source = event.getEntityLiving();

        if (target instanceof Player)
        {
            Player player = (Player) target;
            IMorphing morphing = Morphing.get(player);

            if (morphing == null || !morphing.isMorphed())
            {
                return;
            }
            
            AbstractMorph currentMorph = morphing.getCurrentMorph();

            if (morphing.getCurrentMorph().getSettings().hostile && source.getAttackingEntity() != target && 
            !(currentMorph instanceof bryanthedragon.morph.api.morphs.EntityMorph && ((bryanthedragon.morph.api.morphs.EntityMorph) currentMorph).getEntity() == source.getAttackingEntity()))
            {
                if (source instanceof EntityLiving)
                {
                    ((EntityLiving) event.getEntity()).setAttackTarget(null);
                }
            }
        }
        else if(target != null)
        {
            List playerList = target.level().getEntitiesWithinAABB(Player.class, new AABB(target.posX-1,target.posY-1,target.posZ-1,target.posX+1,target.posY+1,target.posZ+1));
            for(int i = 0; i < playerList.size(); i++)
            {
                Object o = playerList.get(i);
                if(o instanceof Player)
                {
                    Player player = (Player) o;
                    IMorphing capability = Morphing.get(player);
                    if(capability == null)
                        continue;
                    AbstractMorph currentMorph = capability.getCurrentMorph();
                    if(currentMorph == null)
                        continue;
                    if (currentMorph instanceof bryanthedragon.morph.api.morphs.EntityMorph)
                    {
                        bryanthedragon.morph.api.morphs.EntityMorph currentEntityMorph = (bryanthedragon.morph.api.morphs.EntityMorph) currentMorph;
                        if(currentEntityMorph.getEntity() == target && source instanceof EntityLiving)
                            ((EntityLiving) event.getEntity()).setAttackTarget(player);
                    }
                }
            }
        }
    }

    /**
     * Make sure the player dimension and morph dimension are synced
     */
    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerChangedDimensionEvent event)
    {
        IMorphing capability = Morphing.get(event.player);

        if (capability != null && capability.getCurrentMorph() != null)
        {
            capability.getCurrentMorph().onChangeDimension(event.player, event.fromDim, event.toDim);
        }
    }

    /**
     * Run future tasks on both client and server. 
     */
    private void runFutureTasks(Player player)
    {
        if (player.world.isRemote && !FUTURE_TASKS_CLIENT.isEmpty())
        {
            FUTURE_TASKS_CLIENT.remove(0).run();
        }

        if (!player.world.isRemote && !FUTURE_TASKS_SERVER.isEmpty())
        {
            FUTURE_TASKS_SERVER.remove(0).run();
        }
    }
}
