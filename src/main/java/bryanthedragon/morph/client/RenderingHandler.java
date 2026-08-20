package bryanthedragon.morph.client;

import bryanthedragon.morph.ClientProxy;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.morphs.main.EntityMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.client.gui.overlays.GuiHud;
import bryanthedragon.morph.client.gui.overlays.GuiOverlay;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.Team.Visibility;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
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

    public RenderingHandler(GuiOverlay morphOverlay, GuiHud hud)
    {
        this.morphOverlay = morphOverlay;
        this.hud = hud;
    }

    /**
     * Draw HUD additions
     */
    @SubscribeEvent
    public void onHUDRender(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            this.morphOverlay.render(event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
        }
    }

    /**
     * Draw replacement air bar for morphs that can't breathe on land
     */
    @SubscribeEvent
    public void onAirRenderPre(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.AIR || event.isCanceled())
        {
            return;
        }

        if (this.hud.renderSquidAir)
        {
            event.setCanceled(true);

            this.hud.renderSquidAir(event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
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
        Player player = event.getPlayer();
        IMorphing capability = Morphing.get(player);

        /* No morph, no problem */
        if (capability == null)
        {
            return;
        }

        int animation = capability.getAnimation();

        /* Render the morph */
        if (capability.renderPlayer(player, event.getX(), event.getY(), event.getZ(), player.getYRot(), event.getPartialTick()))
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

            GlStateManager.color(1, 1, 1, anim);
        }
    }

    /**
     * On name render, simply render the name of the user, instead of the name of 
     * the entity.
     */
    @SubscribeEvent
    public void onNameRender(RenderLivingEvent.Specials.Pre<LivingEntity> event)
    {
        LivingEntity host = EntityMorph.renderEntity;

        if (host == null)
        {
            return;
        }

        event.setCanceled(true);

        LivingEntity target = event.getEntity();
        Entity camera = Minecraft.getInstance().getRenderViewEntity();

        if (!this.canRenderName(host) || camera == null)
        {
            return;
        }

        double dist = target.distanceToSqr(camera);
        float factor = target.isShiftKeyDown() ? 32.0F : 64.0F;

        if (dist < factor * factor)
        {
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderEntityName(target, host.getDisplayName().getString(), event.getX(), event.getY(), event.getZ());
        }
    }

    /**
     * Can render the morph's name
     */
    protected boolean canRenderName(LivingEntity host)
    {
        Player Playersp = Minecraft.getInstance().player;
        boolean flag = !host.isInvisibleTo(Playersp);

        if (host != Playersp)
        {
            Team team = host.getTeam();
            Team team1 = Playersp.getTeam();

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

        return !Minecraft.getInstance().options.hideGui && host != Minecraft.getInstance().getCameraEntity() && flag && !host.isPassenger();
    }

    /**
     * Renders an entity's name above its head (copied and modified from
     * {@link RenderLivingBase})
     */
    protected void renderEntityName(LivingEntity entity, String name, double x, double y, double z)
    {
        if (name.isEmpty())
        {
            return;
        }

        boolean sneaking = entity.isShiftKeyDown();
        boolean thirdFrontal = Minecraft.getInstance().options.getCameraType().isMirrored();

        float px = Minecraft.getInstance().cameraEntity.getYRot();
        float py = Minecraft.getInstance().cameraEntity.getXRot();
        float pz = entity.getBbHeight() + 0.5F - (sneaking ? 0.25F : 0.0F);

        int i = "deadmau5".equals(name) ? -10 : 0;

        EntityRenderer.drawNameplate(Minecraft.getInstance().font, name, (float) x, (float) y + pz, (float) z, i, px, py, thirdFrontal, sneaking);
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        MorphManager.INSTANCE.list.reset();
        ClientProxy.survivalScreen = null;
    }
}