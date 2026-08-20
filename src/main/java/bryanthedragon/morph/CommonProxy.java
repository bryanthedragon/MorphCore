package bryanthedragon.morph;

import bryanthedragon.morph.api.MorphHandler;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.RegisterHandler;
import bryanthedragon.morph.capabilities.CapabilityHandler;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.render.IModelRenderer;
import bryanthedragon.morph.entity.SoundHandler;
import bryanthedragon.morph.network.Dispatcher;
import bryanthedragon.vanilla_pack.MorphCoreFactory;

import net.minecraft.world.entity.player.Player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;

/**
 * Common proxy
 * 
 * This proxy is responsible for number of things. For registering network 
 * messages, event handlers and capabilities. It also responsible for loading 
 * models.
 */
public class CommonProxy
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

    public CommonProxy()
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