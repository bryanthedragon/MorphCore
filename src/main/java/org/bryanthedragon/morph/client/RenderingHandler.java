package org.bryanthedragon.morph.client;

import org.bryanthedragon.morph.MorphCoreProxy.ClientCoreProxy;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.morphs.main.EntityMorph;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.client.gui.overlays.GuiHud;
import org.bryanthedragon.morph.client.gui.overlays.GuiOverlay;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.Team.Visibility;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Rendering handler
 *
 * This handler is rendering handler which is responsible for few things:
 * 
 * - Overlays (survival morph menu, morph acquiring)
 * - Player model
 */
@OnlyIn(Dist.CLIENT)
public class RenderingHandler
{
    private GuiOverlay morphOverlay;
    private GuiHud hud;

    public RenderingHandler()
    {

    }

    public RenderingHandler(GuiOverlay morphOverlay, GuiHud hud)
    {
        this.morphOverlay = morphOverlay;
        this.hud = hud;
    }

    /**
     * Draw HUD additions
     */
    @SubscribeEvent
    public void onHUDRender(RenderGuiEvent.Post event)
    {
        this.morphOverlay.render(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
    }

    /**
     * Draw replacement air bar for morphs that can't breathe on land
     */
    @SubscribeEvent
    public void onAirRenderPre(RenderGuiOverlayEvent.Pre event)
    {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.AIR_LEVEL.id()) || event.isCanceled())
        {
            return;
        }

        if (this.hud.renderSquidAir)
        {
            event.setCanceled(true);

            this.hud.renderSquidAir(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
        }
    }

    /**
     * Render player hook
     * 
     * This method is responsible for rendering player, in case if he's morphed, 
     * into morphed entity. This method is also responsible for down scaling
     * oversized entities in inventory GUIs.
     * 
     * I wish devs at Mojang scissored the inventory area where those the 
     * player model is rendered. 
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        Player player = event.getEntity();
        IMorphing capability = Morphing.get(player);

        /* No morph, no problem */
        if (capability == null)
        {
            return;
        }

        int animation = capability.getAnimation();

        /* Render the morph */
        if (capability.renderPlayer(event.getPoseStack(), player, 0, 0, 0, player.getYRot(), event.getPartialTick()))
        {
            event.setCanceled(true);
        }
        else if (capability.isAnimating())
        {
            float partialTick = event.getPartialTick();
            float anim = 0;

            if (capability.getCurrentMorph() == null && animation <= 10)
            {
                anim = 1 - (animation - partialTick) / 10.0F;
            }
            else if (capability.getPreviousMorph() == null && animation > 10)
            {
                anim = (animation - 10 - partialTick) / 10.0F;
            }

            RenderSystem.setShaderColor(1, 1, 1, anim);
        }
    }

    /**
     * On name render, simply render the name of the user, instead of the name of 
     * the entity.
     */
    @SubscribeEvent
    public void onNameRender(RenderNameTagEvent event)
    {
        LivingEntity host = EntityMorph.renderEntity;

        if (host == null || !(event.getEntity() instanceof LivingEntity target))
        {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();

        if (!this.canRenderName(host) || camera == null)
        {
            event.setCanceled(true);

            return;
        }

        double dist = target.distanceToSqr(camera);
        float factor = target.isShiftKeyDown() ? 32.0F : 64.0F;

        if (dist < factor * factor)
        {
            event.setContent(host.getDisplayName());
        }
        else
        {
            event.setCanceled(true);
        }
    }

    /**
     * Can render the morph's name
     */
    protected boolean canRenderName(LivingEntity host)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null)
        {
            return false;
        }

        boolean flag = !host.isInvisibleTo(player);

        if (host != player)
        {
            Team team = host.getTeam();
            Team team1 = player.getTeam();

            if (team != null)
            {
                Visibility team$enumvisible = team.getNameTagVisibility();

                switch (team$enumvisible)
                {
                    case ALWAYS:
                        return flag;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null ? flag : team.isAlliedTo(team1) && (team.canSeeFriendlyInvisibles() || flag);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null ? flag : !team.isAlliedTo(team1) && flag;
                    default:
                        return true;
                }
            }
        }

        if (!(host instanceof Player))
        {
            flag = flag && host.hasCustomName();
        }

        return !minecraft.options.hideGui && host != minecraft.getCameraEntity() && flag && !host.isPassenger();
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event)
    {
        MorphManager.INSTANCE.list.reset();
        ClientCoreProxy.survivalScreen = null;
    }
}