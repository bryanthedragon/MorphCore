package bryanthedragon.morph.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import io.netty.buffer.ByteBuf;

import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.client.gui.utils.ValueColors;
import bryanthedragon.morph.config.json.ConfigParser;
import bryanthedragon.morph.config.values.Value;
import bryanthedragon.morph.config.values.ValueBoolean;
import bryanthedragon.morph.config.values.ValueDouble;
import bryanthedragon.morph.config.values.ValueFloat;
import bryanthedragon.morph.config.values.ValueInt;
import bryanthedragon.morph.config.values.ValueRL;
import bryanthedragon.morph.config.values.ValueString;
import bryanthedragon.morph.events.RegisterConfigEvent;
import bryanthedragon.morph.network.morphlib.Dispatcher;
import bryanthedragon.morph.network.morphlib.common.PacketConfig;
import bryanthedragon.morph.utils.bytebuf.ByteBufUtils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager
{
    public static final BiMap<String, Class<? extends Value>> TYPES = HashBiMap.<String, Class<? extends  Value>>create();

    public final Map<String, Config> modules = new HashMap<String, Config>();

    static
    {
        TYPES.put("boolean", ValueBoolean.class);
        TYPES.put("double", ValueDouble.class);
        TYPES.put("float", ValueFloat.class);
        TYPES.put("int", ValueInt.class);
        TYPES.put("rl", ValueRL.class);
        TYPES.put("string", ValueString.class);
        TYPES.put("colors", ValueColors.class);
    }

    public static void synchronizeConfig(Config config, MinecraftServer server)
    {
        synchronizeConfig(config, server, null);
    }

    /**
     * Send given config to all players on the server
     */
    public static void synchronizeConfig(Config config, MinecraftServer server, Player exception)
    {
        for (ServerPlayer target : server.getPlayerList().getPlayers())
        {
            if (target == exception)
            {
                continue;
            }

            Dispatcher.sendTo(new PacketConfig(config, true), target);
        }
    }

    /**
     * Config value to bytes
     */
    public static Value fromBytes(ByteBuf buffer)
    {
        String key = ByteBufUtils.readUTF8String(buffer);
        String type = ByteBufUtils.readUTF8String(buffer);

        if (type.isEmpty())
        {
            return null;
        }

        try
        {
            Class<? extends Value> clazz = TYPES.get(type);
            Value value = clazz.getConstructor(String.class).newInstance(key);

            value.fromBytes(buffer);

            return value;
        }
        catch (Exception e)
        {}

        return null;
    }

    /**
     * Config value from bytes
     */
    public static void toBytes(ByteBuf buffer, Value value)
    {
        String type = TYPES.inverse().get(value.getClass());

        ByteBufUtils.writeUTF8String(buffer, value.id);
        ByteBufUtils.writeUTF8String(buffer, type == null ? "" : type);

        if (type != null)
        {
            value.toBytes(buffer);
        }
    }

    public void register(File configs)
    {
        RegisterConfigEvent event = new RegisterConfigEvent(configs);

        MorphCore.EVENT_BUS.post(event);

        Config opAccess = event.opAccess.getConfig().serverSide();

        this.modules.put(opAccess.id, opAccess);

        for (Config config : event.modules)
        {
            this.modules.put(config.id, config);
        }

        this.reload();
    }

    public void reload()
    {
        for (Config config : this.modules.values())
        {
            ConfigParser.fromJson(config, config.file);
        }
    }

    public void resetServerValues()
    {
        for (Config config : this.modules.values())
        {
            config.resetServerValues();
        }
    }
}