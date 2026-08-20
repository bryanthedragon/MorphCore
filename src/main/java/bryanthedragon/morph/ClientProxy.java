package bryanthedragon.morph;

import bryanthedragon.morph.utils.OpHelper;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.client.EntityModelHandler;
import bryanthedragon.morph.client.KeyboardHandler;
import bryanthedragon.morph.client.NetworkHandler;
import bryanthedragon.morph.client.RenderingHandler;
import bryanthedragon.morph.client.gui.overlays.GuiHud;
import bryanthedragon.morph.client.gui.overlays.GuiOverlay;
import bryanthedragon.morph.client.gui.survival.GuiSurvivalScreen;
import bryanthedragon.morph.client.render.RenderMorph;
import bryanthedragon.morph.entity.EntityMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Client proxy
 * 
 * Client proxy is responsible for adding some rendering modifications (i.e. 
 * HUD morph panel and player rendering) and also responsible for loading 
 * (constructing ModelCustom out of) custom models. 
 */
@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy
{
    /**
     * GUI overlay which is responsible for showing up acquired morphs
     */
    public static GuiOverlay morphOverlay = new GuiOverlay();

    /**
     * Cached survival screen
     */
    public static GuiSurvivalScreen survivalScreen;
    
    public static GuiHud hud = new GuiHud();

    /**
     * Keyboard handler 
     */
    public static KeyboardHandler keys;

    /**
     * Entity model handler
     */
    public static EntityModelHandler models;

    public static GuiSurvivalScreen getSurvivalScreen()
    {
        if (survivalScreen == null)
        {
            survivalScreen = new GuiSurvivalScreen();
        }

        return survivalScreen;
    }

    @Override
    public void preInit(Object event)
    {
        super.preInit(event);

        /* Registering an event channel for custom payload */
        MorphCore.channel.register(0, (friendlyByteBuf, networkEventContext) -> { new NetworkHandler().handle(friendlyByteBuf, networkEventContext); return true; });
    }

    @Override
    public void init()
    {
        /* Continue loading process */
        super.init();

        /* Register client event handlers */
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(morphOverlay, hud));
        MinecraftForge.EVENT_BUS.register(keys = new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(models = new EntityModelHandler());

        models.loadSelectors();

        if (!this.selectors.exists())
        {
            MorphUtils.generateFile(this.selectors, "[]");
        }

        if (!this.list.exists())
        {
            MorphUtils.generateFile(this.selectors, "[]");
        }
    }

    @SubscribeEvent
    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(EntityMorph.TYPE, RenderMorph::new);
    }

    @Override
    public boolean canEditSelectors()
    {
        return OpHelper.isPlayerOp() || MorphCore.opEntitySelector.get();
    }
}