package bryanthedragon.morph.capabilities;

import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.capabilities.morphing.MorphingProvider;
import bryanthedragon.morph.capabilities.render.IModelRenderer;
import bryanthedragon.morph.capabilities.render.ModelProvider;
import bryanthedragon.morph.capabilities.render.ModelRenderer;
import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.morph.network.common.survival.PacketAcquiredMorphs;
import bryanthedragon.morph.network.common.PacketBlacklist;
import bryanthedragon.morph.network.common.creative.PacketMorph;
import bryanthedragon.morph.network.common.survival.PacketMorphPlayer;
import bryanthedragon.morph.network.common.survival.PacketMorphState;
import bryanthedragon.morph.network.common.PacketSettings;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.event.AttachCapabilitiesEvent;
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

        if (event.getObject() instanceof LivingEntity && event.getObject().world != null && event.getObject().world.isRemote)
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
        Player player = event.player;
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
            ServerPlayer player = (ServerPlayer) event.getPlayer();
            IMorphing cap = target.getCapability(MorphingProvider.MORPHING_CAP, null);

            Dispatcher.sendTo(new PacketMorphPlayer(target.getEntityId(), cap.getCurrentMorph()), player);
        }
    }

    /**
     * On player's spawn in the world (when player travels in other dimension 
     * and spawns there or when player dies and then respawns).
     * 
     * This method is responsible for sending morphing data on the client.
     */
    @SubscribeEvent
    public void onPlayerSpawn(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();

        if (entity instanceof Player && !entity.world.isRemote)
        {
            Player player = (Player) entity;
            IMorphing morphing = Morphing.get(player);

            this.sendAcquiredMorphs(morphing, player);
            Dispatcher.sendTo(new PacketMorphState(player, morphing), (ServerPlayer) player);
        }
        else if (entity instanceof LivingEntity && entity.world.isRemote)
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
        Player player = event.getPlayer();
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