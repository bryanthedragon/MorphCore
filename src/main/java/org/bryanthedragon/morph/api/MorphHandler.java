package bryanthedragon.morph.api;

import bryanthedragon.morph.MorphCoreProxy.ClientCoreProxy;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.events.SpawnGhostEvent;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
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
        if (player.level().isClientSide)
        {
            boolean hasSquidAir = false;
            int squidAir = 300;

            {
                hasSquidAir = capability.getHasSquidAir();
                squidAir = capability.getSquidAir();
            }

            ClientCoreProxy.hud.renderSquidAir = hasSquidAir;
            ClientCoreProxy.hud.squidAir = squidAir;
        }

        try
        {
            capability.update(player);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            if (!player.level().isClientSide)
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
        Entity source = event.getSource().getEntity();
        Entity target = event.getEntity();

        if (target.level().isClientSide || source instanceof FakePlayer)
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

        tag.putString("Name", name);
        tag.put("EntityData", EntityUtils.stripEntityNBT(target.serializeNBT()));

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

            bryanthedragon.morph.entity.EntityMorph morphEntity = new bryanthedragon.morph.entity.EntityMorph(null, player.level(), player.getUUID(), morph);

            morphEntity.setPos(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
            morphEntity.setYRot(target.getYRot());
            morphEntity.setXRot(target.getXRot());
            player.level().addFreshEntity(morphEntity);

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
            
            if (morph == null || !(morph instanceof EntityMorph))
            {
                return;
            }
            
            EntityMorph entityMorph = (EntityMorph)morph;
            
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
        Entity trueSource = source.getEntity();
        Entity target = event.getEntity();

        if (source.getDirectEntity() != trueSource)
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
    @SubscribeEvent
    public void onLivingSetAttackTarget(LivingChangeTargetEvent event)
    {
        if (MorphCore.disableMorphDisguise.get())
        {
            return;
        }

        LivingEntity target = event.getNewTarget();
        LivingEntity source = event.getEntity();

        if (target instanceof Player)
        {
            Player player = (Player) target;
            IMorphing morphing = Morphing.get(player);

            if (morphing == null || !morphing.isMorphed())
            {
                return;
            }
            
            AbstractMorph currentMorph = morphing.getCurrentMorph();

            if (morphing.getCurrentMorph().getSettings().hostile && source.getLastHurtByMob() != target && 
            !(currentMorph instanceof EntityMorph && ((EntityMorph) currentMorph).getEntity() == source.getLastHurtByMob()))
            {
                event.setNewTarget(null);
            }
        }
        else if(target != null)
        {
            List<Player> playerList = target.level().getEntitiesOfClass(Player.class, target.getBoundingBox().inflate(1.0D));
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
                    if (currentMorph instanceof EntityMorph)
                    {
                        EntityMorph currentEntityMorph = (EntityMorph) currentMorph;
                        if(currentEntityMorph.getEntity() == target)
                            event.setNewTarget(player);
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
        Player player = (Player) event.getEntity();
        IMorphing capability = Morphing.get(player);

        if (capability != null && capability.getCurrentMorph() != null)
        {
            capability.getCurrentMorph().onChangeDimension(player, 0, 0);
        }
    }

    /**
     * Run future tasks on both client and server. 
     */
    private void runFutureTasks(Player player)
    {
        if (player.level().isClientSide && !FUTURE_TASKS_CLIENT.isEmpty())
        {
            FUTURE_TASKS_CLIENT.remove(0).run();
        }

        if (!player.level().isClientSide && !FUTURE_TASKS_SERVER.isEmpty())
        {
            FUTURE_TASKS_SERVER.remove(0).run();
        }
    }
}
