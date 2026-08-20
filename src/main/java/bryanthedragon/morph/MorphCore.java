package bryanthedragon.morph;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import bryanthedragon.morph.config.ConfigBuilder;
import bryanthedragon.morph.config.values.ValueBoolean;
import bryanthedragon.morph.config.values.ValueInt;
import bryanthedragon.morph.config.values.ValueRL;
import bryanthedragon.morph.events.RegisterConfigEvent;
import bryanthedragon.morph.events.RegisterPermissionsEvent;
import bryanthedragon.morph.math.IValue;
import bryanthedragon.morph.math.MathBuilder;
import bryanthedragon.morph.math.Variable;
import bryanthedragon.morph.permissions.DefaultPermissionLevel;
import bryanthedragon.morph.permissions.MorphLibPermissions;
import bryanthedragon.morph.permissions.PermissionCategory;
import bryanthedragon.morph.permissions.PermissionFactory;
import bryanthedragon.morph.utils.ColorUtils;
import bryanthedragon.morph.utils.PayloadASM;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.client.gui.utils.ValueColors;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.commands.CommandCheats;
import bryanthedragon.morph.commands.CommandMorphLib;
import bryanthedragon.morph.commands.utils.L10n;
import bryanthedragon.morph.math.Operation;
import bryanthedragon.morph.math.Operator;

import org.apache.logging.log4j.LogManager;

import java.util.Map;

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

    public static final EventBus EVENT_BUS = new EventBus();

    public static final String CLIENT_PROXY = "bryanthedragon.morph.ClientProxy";
    public static final String SERVER_PROXY = "bryanthedragon.morph.CommonProxy";

    /* Forge stuff classes */
    public static CommonProxy proxy;

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
        
        // Initialize proxy
        proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        
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
        proxy.preInit(null);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        proxy.init();
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event)
    {
        /* Setting up the blacklist */
        MorphManager.INSTANCE.setActiveBlacklist(null, MorphUtils.reloadBlacklist());
        MorphManager.INSTANCE.setActiveSettings(MorphUtils.reloadMorphSettings());
        MorphManager.INSTANCE.setActiveMap(MorphUtils.reloadRemapper());

        /* Register commands - temporarily commented out until updated to Brigadier */
        // CommandMorph.register(event.getServer().getCommands().getDispatcher());
        // CommandAcquireMorph.register(event.getServer().getCommands().getDispatcher());
        // CommandMorphCore.register(event.getServer().getCommands().getDispatcher());
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

    public static class MorphLib
    {
        public static final String MOD_ID = "MorphLib";
        public static final String VERSION = "%VERSION%";
        public static final String UPDATE_JSON = "";

        /* Proxies */
        public static final String CLIENT_PROXY = "bryanthedragon.morph.ClientProxy";
        public static final String SERVER_PROXY = "bryanthedragon.morph.CommonProxy";

        public static CommonProxy proxy;

        public static final Logger LOGGER = LogManager.getLogger(MorphLib.MOD_ID);

        public MorphLib()
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConstructMod);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            MinecraftForge.EVENT_BUS.register(this);
            
            // Initialize proxy
            proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        }

        public static L10n l10n = new L10n(MOD_ID);

        /**
         * A factory containing all permissions that have been registered through the {@link RegisterPermissionsEvent}.
         */
        public static final PermissionFactory permissionFactory = new PermissionFactory();

        /* Configuration */
        public static ValueBoolean opDropItems;

        public static ValueBoolean debugPanel;
        public static ValueColors favoriteColors;
        public static ValueInt primaryColor;
        public static ValueBoolean enableBorders;
        public static ValueBoolean enableCheckboxRendering;
        public static ValueBoolean enableTrackpadIncrements;
        public static ValueBoolean enableGridRendering;
        public static ValueInt userIntefaceScale;
        public static ValueInt tooltipStyle;
        public static ValueInt trackpadDecimalPlaces;
        public static ValueBoolean renderTranslateTextColors;

        public static ValueBoolean enableCursorRendering;
        public static ValueBoolean enableMouseButtonRendering;
        public static ValueBoolean enableKeystrokeRendering;
        public static ValueInt keystrokeOffset;
        public static ValueInt keystrokeMode;

        public static ValueRL backgroundImage;
        public static ValueInt backgroundColor;

        public static ValueBoolean scrollbarFlat;
        public static ValueInt scrollbarShadow;
        public static ValueInt scrollbarWidth;

        public static ValueBoolean multiskinMultiThreaded;
        public static ValueBoolean multiskinClear;

        public static ValueInt maxPacketSize;

        @SubscribeEvent
        public void onConfigRegister(RegisterConfigEvent event)
        {
            opDropItems = event.opAccess.category(MOD_ID).getBoolean("drop_items", true);

            /* MorphLib's options */
            ConfigBuilder builder = event.createBuilder(MOD_ID);

            /* Appearance category */
            debugPanel = builder.category("appearance").getBoolean("debug_panel", false);
            debugPanel.invisible();
            primaryColor = builder.getInt("primary_color", 0x0088ff).color();
            enableBorders = builder.getBoolean("enable_borders", false);
            enableCheckboxRendering = builder.getBoolean("enable_checkbox_rendering", false);
            enableTrackpadIncrements = builder.getBoolean("enable_trackpad_increments", true);
            trackpadDecimalPlaces = builder.getInt("trackpad_decimal_places", 6, 3, 31);
            enableGridRendering = builder.getBoolean("enable_grid_rendering", true);
            userIntefaceScale = builder.getInt("user_interface_scale", 2, 0, 4);
            tooltipStyle = builder.getInt("tooltip_style", 1).modes(IKey.lang("MorphLib.tooltip_style.light"), IKey.lang("MorphLib.tooltip_style.dark"));
            renderTranslateTextColors = builder.getBoolean("render_translation_text_colours", false);

            favoriteColors = new ValueColors("favorite_colors");
            builder.register(favoriteColors);

            builder.getCategory().markClientSide();

            /* Tutorials category */
            enableCursorRendering = builder.category("tutorials").getBoolean("enable_mouse_rendering", false);
            enableMouseButtonRendering = builder.getBoolean("enable_mouse_buttons_rendering", false);
            enableKeystrokeRendering = builder.getBoolean("enable_keystrokes_rendering", false);
            keystrokeOffset = builder.getInt("keystroke_offset", 10, 0, 20);
            keystrokeMode = builder.getInt("keystroke_position", 1).modes(IKey.lang("MorphLib.keystrokes_position.auto"), IKey.lang("MorphLib.keystrokes_position.bottom_left"), IKey.lang("MorphLib.keystrokes_position.bottom_right"), IKey.lang("MorphLib.keystrokes_position.top_right"), IKey.lang("MorphLib.keystrokes_position.top_left"));

            builder.getCategory().markClientSide();

            /* Background category */
            backgroundImage = builder.category("background").getRL("image",  null);
            backgroundColor = builder.getInt("color",  0xcc000000).colorAlpha();

            builder.getCategory().markClientSide();

            /* Scrollbars category */
            scrollbarFlat = builder.category("scrollbars").getBoolean("flat", false);
            scrollbarShadow = builder.getInt("shadow", ColorUtils.HALF_BLACK).colorAlpha();
            scrollbarWidth = builder.getInt("width", 4, 2, 10);

            builder.getCategory().markClientSide();

            /* Multiskin category */
            multiskinMultiThreaded = builder.category("multiskin").getBoolean("multithreaded", true);
            multiskinClear = builder.getBoolean("clear", true);

            builder.getCategory().markClientSide();

            /* Vanilla category */
            maxPacketSize = builder.category("vanilla").getInt("max_packet_size", PayloadASM.MIN_SIZE, PayloadASM.MIN_SIZE, Integer.MAX_VALUE / 4);
            maxPacketSize.syncable();
        }

        @SubscribeEvent
        public void onPermissionRegister(RegisterPermissionsEvent event)
        {
            event.registerMod(MOD_ID, DefaultPermissionLevel.OP);

            event.registerPermission(MorphLibPermissions.configEdit = new PermissionCategory("edit_config"));

            event.registerCategory(new PermissionCategory("gui"));
            event.registerPermission(MorphLibPermissions.accessGui = new PermissionCategory("access_gui"));

            event.endMod();
        }

        private void onConstructMod(FMLConstructModEvent event)
        {
            proxy.preInit(null);
        }

        private void onCommonSetup(FMLCommonSetupEvent event)
        {
            proxy.init();
        }

        @SubscribeEvent
        public void serverInit(ServerStartingEvent event)
        {
            if (event.getServer().isSingleplayer())
            {
                CommandCheats.register(event.getServer().getCommands().getDispatcher());
            }

            CommandMorphLib.register(event.getServer().getCommands().getDispatcher());
        }

        public static void main(String[] args) throws Exception
        {
            Operator.DEBUG = true;
            MathBuilder builder = new MathBuilder();

            test(builder, "1 - 2 * 3 + 4 ", 1 - 2 * 3 + 4  );
            test(builder, "2 * 3 - 8 + 7 ", 2 * 3 - 8 + 7  );
            test(builder, "3 - 7 + 2 * 4 ", 3 - 7 + 2 * 4  );
            test(builder, "8 / 4 - 3 * 10", 8 / 4 - 3 * 10 );
            test(builder, "2 - 4 * 5 / 8 ", 2 - 4 * 5 / 8D );
            test(builder, "3 / 4 * 8 - 10", 3 / 4D * 8 - 10);
            test(builder, "2 * 3 / 4 * 5 ", 2D * 3 / 4 * 5 );
            test(builder, "2 + 3 - 4 + 5 ", 2 + 3 - 4 + 5  );
            test(builder, "7 - 2 ^ 4 - 4 * 5 + 15 ^ 2", 7 - Math.pow(2, 4) - 4 * 5 + Math.pow(15, 2));
            test(builder, "5 -(10 + 20)", 5 -(10 + 20));
            test(builder, "1 << 4 - 1", 1 << 4 - 1);
            test(builder, "256 >> 4 + 2", 256 >> 4 + 2);
            test(builder, "255 & 7 + 1", 255 & 7 + 1);
            test(builder, "256 | 7 + 1", 256 | 7 + 1);
            test(builder, "5 % 2 + 1 == 0 * 2", 5 % 2 + 1 == 0 * 2 ? 1 : 0);

            builder.variables.put("abc", new Variable("abc", 1));
            IValue test = builder.parse("- (40 + 2) / -2");

            System.out.println(test.isNumber() + " " + test.stringValue() + " " + test.booleanValue() + " " + test.doubleValue());
        }

        public static void test(MathBuilder builder, String expression, double result) throws Exception
        {
            IValue value = builder.parse(expression);

            System.out.println(expression + " = " + value.get() + " (" + result + ") is " + Operation.equals(value.get().doubleValue(), result));
            System.out.println(value.toString() + "\n");
        }
    }
}