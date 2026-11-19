package org.bryanthedragon.morph;

import org.bryanthedragon.morph.api.MorphHandler;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.utils.morph.MorphUtils;
import org.bryanthedragon.morph.api.RegisterHandler;
import org.bryanthedragon.morph.api.morphs.main.MorphPickupEntity;
import org.bryanthedragon.morph.capabilities.CapabilityHandler;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.render.IModelRenderer;
import org.bryanthedragon.morph.client.EntityModelHandler;
import org.bryanthedragon.morph.client.NetworkHandler;
import org.bryanthedragon.morph.client.gui.overlays.GuiHud;
import org.bryanthedragon.morph.client.gui.overlays.GuiOverlay;
import org.bryanthedragon.morph.client.gui.survival.GuiSurvivalScreen;
import org.bryanthedragon.morph.client.render.RenderMorph;
import org.bryanthedragon.morph.core.registry.MorphCoreFactory;
import org.bryanthedragon.morph.entity.SoundHandler;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.utils.helpers.op.OpHelper;
import org.bryanthedragon.morph.client.KeyboardHandler;
import org.bryanthedragon.morph.client.RenderingHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;

/**
 * Common proxy
 * 
 * This proxy is responsible for number of things. For registering network 
 * messages, event handlers and capabilities. It also responsible for loading 
 * models.
 */
public class MorphCoreProxy
{
    public static class PluginPayload
    {
        public final byte[] data;

        public PluginPayload(byte[] data)
        {
            this.data = data;
        }

        public void encode(FriendlyByteBuf buffer)
        {
            buffer.writeBytes(this.data);
        }

        public static PluginPayload decode(FriendlyByteBuf buffer)
        {
            byte[] data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);

            return new PluginPayload(data);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientCoreProxy extends CommonCoreProxy
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
            MorphCore.channel.registerMessage(0, PluginPayload.class, PluginPayload::encode, PluginPayload::decode, (payload, contextSupplier) ->
            {
                NetworkEvent.Context context = contextSupplier.get();
                new NetworkHandler().handle(new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(payload.data)), context);
                context.setPacketHandled(true);
            }, java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
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
                org.bryanthedragon.morph.api.utils.morph.MorphUtils.generateFile(this.selectors, "[]");
            }
        }

        @SubscribeEvent
        public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
        {
            event.registerEntityRenderer(MorphPickupEntity.TYPE.get(), RenderMorph::new);
        }

        @Override
        public boolean canEditSelectors()
        {
            return OpHelper.isPlayerOp() || MorphCore.opEntitySelector.get();
        }
    }
    public static class CommonCoreProxy
    {
        /**
         * Location of a user's morph settings
         */
        public File morphs;

        /**
         * Location of a user's morph blacklist
         */
        public File blacklist;

        /**
         * Location of a user's morph ID remapper
         */
        public File remap;

        /**
         * Location of a user's entity selectors (client side)
         */
        public File selectors;

        /**
         * Location of a user's custom global morph list (client side)
         */
        public File list;

        public CommonCoreProxy()
        {
            
        }

        public void preInit(Object event)
        {
            /* Network messages */
            Dispatcher.register();

            /* Attaching morph factory to the morph manager */
            MorphManager.INSTANCE.factories.add(new MorphCoreFactory());

            /* Configuration */
            File configDir = new File("./config");
            this.morphs = new File(configDir, "morphcore/morphs.json");
            this.blacklist = new File(configDir, "morphcore/blacklist.json");
            this.remap = new File(configDir, "morphcore/remap.json");
            this.selectors = new File(configDir, "morphcore/selectors.json");
            this.list = new File(configDir, "morphcore/list.json");
        }

        /**
         * Load stuff
         * 
         * Add event listeners, register morphing capability and also load user 
         * configuration. I don't know how it's going to work in multiplayer, 
         * probably won't lol
         */
        public void init()
        {
            /* Event listeners */
            MinecraftForge.EVENT_BUS.register(new MorphHandler());
            MinecraftForge.EVENT_BUS.register(new SoundHandler());
            MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
            MinecraftForge.EVENT_BUS.register(new RegisterHandler());

            /* Register morph factories */
            MorphManager.INSTANCE.register();

            /* User configuration */
            if (!this.morphs.exists())
            {
                MorphUtils.generateFile(this.morphs, "{}");
            }

            if (!this.blacklist.exists())
            {
                MorphUtils.generateFile(this.blacklist, "[]");
            }

            if (!this.remap.exists())
            {
                MorphUtils.generateFile(this.remap, "{}");
            }
        }

        @SubscribeEvent
        public void onRegisterCapabilities(RegisterCapabilitiesEvent event)
        {
            event.register(IMorphing.class);
            event.register(IModelRenderer.class);
        }

        public boolean canUse(Player player)
        {
            return player.isCreative() || MorphCore.allowMorphingIntoCategoryMorphs.get();
        }

        public boolean canEditSelectors()
        {
            return true;
        }
    }
}