package org.bryanthedragon.morph;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import org.bryanthedragon.morph.config.builder.ConfigBuilder;
import org.bryanthedragon.morph.config.values.ValueBoolean;
import org.bryanthedragon.morph.config.values.ValueInt;
import org.bryanthedragon.morph.events.RegisterConfigEvent;
import org.bryanthedragon.morph.MorphCoreProxy.ClientCoreProxy;
import org.bryanthedragon.morph.MorphCoreProxy.CommonCoreProxy;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.morphs.main.MorphPickupEntity;
import org.bryanthedragon.morph.api.utils.morph.MorphUtils;
import org.bryanthedragon.morph.commands.CommandAcquireMorph;
import org.bryanthedragon.morph.commands.CommandMorph;
import org.bryanthedragon.morph.commands.CommandMorphCore;
import org.bryanthedragon.morph.loader.MorphLoader;
import org.bryanthedragon.morph.loader.state.MorphLoaderState;


/**
 * MorphCore mod
 * 
 * This mod provides functionality for survival morphing. To gain a morph 
 * you have to kill a mob. Once you killed it, you gain its morphing. Once you 
 * gained its morphing you can use special menu to select a mob into which to 
 * morph.
 * 
 * Except different shape, you gain also special abilities for specific mobs. 
 * In creative you can access all morphings.
 * 
 * Inspired by Morph and Shape Shifter Z mods (mostly due to the fact that 
 * they're outdated), however, iChun saying that he's working on Morph for 
 * 1.10.2, this is really exciting! :D
 */
@Mod(MorphCore.MOD_ID)
public class MorphCore
{
    /* Metadata fields */
    public static final String MOD_ID = "morphcore";
    public static final String MODNAME = "MorphCore";
    public static final String VERSION = "%VERSION%";

    public static final IEventBus EVENT_BUS = BusBuilder.builder().build();

    public static final String CLIENT_PROXY = "bryanthedragon.morph.ClientProxy";
    public static final String SERVER_PROXY = "bryanthedragon.morph.CommonProxy";

    /* Forge stuff classes */
    public static CommonCoreProxy proxy;

    public static MorphCore instance;

    /**
     * Custom payload channel 
     */
    public static SimpleChannel channel;

    public MorphCore()
    {
        instance = this;
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConstructMod);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MorphPickupEntity.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        // Initialize proxy
        proxy = DistExecutor.unsafeRunForDist(() -> ClientCoreProxy::new, () -> CommonCoreProxy::new);
        
        // Setup network channel
        channel = ChannelBuilder.named(new ResourceLocation(MOD_ID, "main")).networkProtocolVersion(() -> "1").simpleChannel();
    }

    /* Configuration */
    public static ValueBoolean opEntitySelector;

    public static ValueBoolean preventGhosts;
    public static ValueBoolean preventKillAcquire;
    public static ValueBoolean acquireImmediately;

    public static ValueBoolean keepMorphs;
    public static ValueBoolean disablePov;
    public static ValueBoolean disableHealth;
    public static ValueBoolean disableMorphAnimation;
    public static ValueBoolean disableMorphDisguise;
    public static ValueBoolean disableFirstPersonHand;
    public static ValueBoolean morphInTightSpaces;
    public static ValueBoolean showMorphIdleSounds;
    public static ValueBoolean pauseGUIInSP;
    public static ValueBoolean renderBodyPartAxis;
    public static ValueInt maxRecentMorphs;
    public static ValueBoolean allowMorphingIntoCategoryMorphs;
    public static ValueBoolean loadEntityMorphs;

    /* Logging */

    public static boolean DEBUG = false;
    public static Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(MOD_ID);

    /* Events */

    @SubscribeEvent
    public void onConfigRegister(RegisterConfigEvent event)
    {
        opEntitySelector = event.opAccess.category(MOD_ID).getBoolean("entity_selectors", true);
        opEntitySelector.syncable();

        /* MorphCore configuration */
        ConfigBuilder builder = event.createBuilder(MOD_ID);

        preventGhosts = builder.category("acquiring").getBoolean("prevent_ghosts", true);
        preventKillAcquire = builder.getBoolean("prevent_kill_acquire", false);
        acquireImmediately = builder.getBoolean("acquire_immediately", false);

        keepMorphs = builder.category("morphs").getBoolean("keep_morphs", true);
        disablePov = builder.getBoolean("disable_pov", false);
        disableHealth = builder.getBoolean("disable_health", false);
        disableMorphAnimation = builder.getBoolean("disable_morph_animation", false);
        disableMorphDisguise = builder.getBoolean("disable_morph_disguise", false);
        disableFirstPersonHand = builder.getBoolean("disable_first_person_hand", false);
        disableFirstPersonHand.clientSide();
        morphInTightSpaces = builder.getBoolean("morph_in_tight_spaces", false);
        showMorphIdleSounds = builder.getBoolean("show_morph_idle_sounds", true);
        pauseGUIInSP = builder.getBoolean("pause_gui_in_sp", true);
        pauseGUIInSP.clientSide();
        renderBodyPartAxis = builder.getBoolean("render_bodypart_axis", true);
        renderBodyPartAxis.clientSide();
        maxRecentMorphs = builder.getInt("max_recent_morphs", 20, 1, 200);
        maxRecentMorphs.clientSide();
        allowMorphingIntoCategoryMorphs = builder.getBoolean("allow_morphing_into_category_morphs", false);
        loadEntityMorphs = builder.getBoolean("load_entity_morphs", true);
        loadEntityMorphs.clientSide();
    }

    private void onConstructMod(FMLConstructModEvent event)
    {
        MorphLoader.instance().setLoaderState(MorphLoaderState.PREINITIALIZATION);
        proxy.preInit(null);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        MorphLoader.instance().setLoaderState(MorphLoaderState.INITIALIZATION);
        proxy.init();
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event)
    {
        /* Setting up the blacklist */
        MorphManager.INSTANCE.setActiveBlacklist(null, MorphUtils.reloadBlacklist());
        MorphManager.INSTANCE.setActiveSettings(MorphUtils.reloadMorphSettings());
        MorphManager.INSTANCE.setActiveMap(MorphUtils.reloadRemapper());

        /* Register commands */
        CommandMorph.register(event.getServer().getCommands().getDispatcher());
        CommandAcquireMorph.register(event.getServer().getCommands().getDispatcher());
        CommandMorphCore.register(event.getServer().getCommands().getDispatcher());
    }

    // /**
    //  * Log out the message if in DEBUG mode.
    //  * 
    //  * But I always forget to turn it off before releasing the mod.
    //  */
    public static void log(String message)
    {
        if (DEBUG)
        {
            LOGGER.log(Level.INFO, message);
        }
    }
}