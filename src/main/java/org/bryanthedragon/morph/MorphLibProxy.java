package org.bryanthedragon.morph;

import org.bryanthedragon.morph.client.InputRenderer;
import org.bryanthedragon.morph.client.KeyboardHandler;
import org.bryanthedragon.morph.client.RenderingHandler;
import org.bryanthedragon.morph.client.gui.utils.Icons;
import org.bryanthedragon.morph.client.gui.utils.KeybindConfig;
import org.bryanthedragon.morph.client.gui.utils.keys.LangKey;
import org.bryanthedragon.morph.config.ConfigHandler;
import org.bryanthedragon.morph.config.ConfigManager;
import org.bryanthedragon.morph.events.RegisterPermissionsEvent;
import org.bryanthedragon.morph.network.morphlib.Dispatcher;
import org.bryanthedragon.morph.utils.reflection.ReflectionUtils;
import org.bryanthedragon.morph.utils.resources.MultiResourceLocation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Client proxy
 * 
 * Client proxy is responsible for adding some rendering modifications (i.e. 
 * HUD morph panel and player rendering) and also responsible for loading 
 * (constructing ModelCustom out of) custom models. 
 */
@OnlyIn(Dist.CLIENT)
public class MorphLibProxy {
    public static class ClientLibProxy extends CommonLibProxy
    {
        public static KeybindConfig keybinds;

        @Override
        public void preInit(FMLConstructModEvent event)
        {
            super.preInit(event);

            MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
            MinecraftForge.EVENT_BUS.register(new InputRenderer());
            MinecraftForge.EVENT_BUS.register(new RenderingHandler());

            keybinds = new KeybindConfig();
        }

        @Override
        public void init(FMLCommonSetupEvent event)
        {
            super.init(event);

            Minecraft mc = Minecraft.getInstance();

            /* OMG, thank you very much Forge! */
            if (!mc.getMainRenderTarget().isStencilEnabled())
            {
                mc.getMainRenderTarget().enableStencil();
            }

            this.configs.modules.put(keybinds.id, keybinds);
        }

        private void clearMultiTextures()
        {
            Minecraft mc = Minecraft.getInstance();
            Map<ResourceLocation, AbstractTexture> map = ReflectionUtils.getTextures((TextureManager) mc.getResourceManager());
            List<ResourceLocation> toClear = new ArrayList<ResourceLocation>();

            for (ResourceLocation location : map.keySet())
            {
                if (location instanceof MultiResourceLocation)
                {
                    toClear.add(location);
                }
            }

            for (ResourceLocation location : toClear)
            {
                mc.textureManager.release(location);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MorphCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientReloadListeners
    {
        @SubscribeEvent
        public static void registerClientReloadListener(RegisterClientReloadListenersEvent event)
        {
            event.registerReloadListener((ResourceManagerReloadListener) manager ->
            {
                LangKey.lastTime = System.currentTimeMillis();

                if (MorphLib.multiskinClear.get() && MorphLib.proxy instanceof ClientLibProxy clientProxy)
                {
                    Minecraft.getInstance().execute(clientProxy::clearMultiTextures);
                }
            });
        }
    }

    public static class CommonLibProxy 
    {
        public ConfigManager configs = new ConfigManager();
        public File configFolder;

        public void preInit(FMLConstructModEvent event)
        {
            this.configFolder = FMLPaths.CONFIGDIR.get().toFile();

            Dispatcher.register();

            MinecraftForge.EVENT_BUS.register(new ConfigHandler());
        }

        public void init(FMLCommonSetupEvent event)
        {
            this.configs.register(this.configFolder);

            RegisterPermissionsEvent permissions = new RegisterPermissionsEvent();

            /* let the mods register their permissions */
            MorphLib.EVENT_BUS.post(permissions);

            permissions.loadPermissions();

            Icons.register();

        }
    }
}