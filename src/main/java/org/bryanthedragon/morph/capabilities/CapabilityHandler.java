package org.bryanthedragon.morph.capabilities;

import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.capabilities.morphing.MorphingProvider;
import org.bryanthedragon.morph.capabilities.render.IModelRenderer;
import org.bryanthedragon.morph.capabilities.render.ModelProvider;
import org.bryanthedragon.morph.capabilities.render.ModelRenderer;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.common.survival.PacketAcquiredMorphs;
import org.bryanthedragon.morph.network.common.PacketBlacklist;
import org.bryanthedragon.morph.network.common.creative.PacketMorph;
import org.bryanthedragon.morph.network.common.survival.PacketMorphPlayer;
import org.bryanthedragon.morph.network.common.survival.PacketMorphState;
import org.bryanthedragon.morph.network.common.PacketSettings;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Capability handler class
 *
 * This class is responsible for managing capabilities, i.e. attaching
 * capabilities and syncing values on the client.
 */
public class CapabilityHandler
{
    public static final ResourceLocation MORPHING_CAP = new ResourceLocation(MorphCore.MOD_ID, "morphing_capability");
    public static final ResourceLocation MODEL_CAP = new ResourceLocation(MorphCore.MOD_ID, "model");

    private static Boolean isMohist;

    private static boolean isMohist()
    {
        if (isMohist != null)
        {
            return isMohist;
        }

        try
        {
            Class.forName("com.mohistmc.MohistMC");

            isMohist = true;
        }
        catch (Exception e)
        {
            isMohist = false;
        }

        return isMohist;
    }

    /**
     * Attach capabilities
     */
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (event == null) return;

        if (event.getObject() instanceof LivingEntity && event.getObject().level() != null && event.getObject().level().isClientSide())
        {
            event.addCapability(MODEL_CAP, new ModelProvider());
        }

        if (event.getObject() instanceof Player)
        {
            event.addCapability(MORPHING_CAP, new MorphingProvider());
        }
    }

    /**
     * When player logs in, sent him his server counter partner's values.
     */
    @SubscribeEvent
    public void playerLogsIn(PlayerLoggedInEvent event)
    {
        Player player = event.getEntity();
        IMorphing cap = Morphing.get(player);

        if (cap != null)
        {
            this.sendAcquiredMorphs(cap, player);

            /* Ensure that player was morphed */
            if (cap.isMorphed())
            {
                cap.getCurrentMorph().morph(player);
            }

            /* Send data */
            Dispatcher.sendTo(new PacketBlacklist(MorphManager.INSTANCE.activeBlacklist), (ServerPlayer) player);
            Dispatcher.sendTo(new PacketSettings(MorphManager.INSTANCE.activeSettings), (ServerPlayer) player);
            Dispatcher.sendTo(new PacketMorphState(player, cap), (ServerPlayer) player);
        }
    }

    /**
     * When player starts tracking another player, server has to send its
     * morphing values.
     */
    @SubscribeEvent
    public void playerStartsTracking(StartTracking event)
    {
        if (event.getTarget() instanceof Player)
        {
            Entity target = event.getTarget();
            ServerPlayer player = (ServerPlayer) event.getEntity();
            IMorphing cap = target.getCapability(MorphingProvider.MORPHING_CAP, null).orElse(null);

            if (cap != null)
            {
                Dispatcher.sendTo(new PacketMorphPlayer(target.getId(), cap.getCurrentMorph()), player);
            }
        }
    }

    /**
     * On player's spawn in the world (when player travels in other dimension 
     * and spawns there or when player dies and then respawns).
     * 
     * This method is responsible for sending morphing data on the client.
     */
    @SubscribeEvent
    public void onPlayerSpawn(EntityJoinLevelEvent event)
    {
        Entity entity = event.getEntity();

        if (entity instanceof Player && !entity.level().isClientSide())
        {
            Player player = (Player) entity;
            IMorphing morphing = Morphing.get(player);

            this.sendAcquiredMorphs(morphing, player);
            Dispatcher.sendTo(new PacketMorphState(player, morphing), (ServerPlayer) player);
        }
        else if (entity instanceof LivingEntity && entity.level().isClientSide())
        {
            IModelRenderer renderer = ModelRenderer.get(entity);

            renderer.updateSelector((LivingEntity) entity);
        }
    }

    /**
     * Copy data from dead player (or player returning from end) to the new player
     */
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        Player player = event.getEntity();
        IMorphing morphing = Morphing.get(player);
        IMorphing oldMorphing = Morphing.get(event.getOriginal());

        if (!isMohist())
        {
            if (MorphCore.keepMorphs.get() || !event.isWasDeath())
            {
                morphing.copy(oldMorphing, player);
            }
        }
    }

    /**
     * Send acquired morphs (and currently morphed morph) to the given player. 
     */
    private void sendAcquiredMorphs(IMorphing cap, Player player)
    {
        ServerPlayer mp = (ServerPlayer) player;

        Dispatcher.sendTo(new PacketMorph(cap.getCurrentMorph()), mp);
        Dispatcher.sendTo(new PacketAcquiredMorphs(cap.getAcquiredMorphs()), mp);
    }
}