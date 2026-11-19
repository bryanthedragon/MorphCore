package org.bryanthedragon.morph.commands;

import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.utils.morph.MorphUtils;
import org.bryanthedragon.morph.api.settings.MorphSettings;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.IMessage;
import org.bryanthedragon.morph.network.common.PacketBlacklist;
import org.bryanthedragon.morph.network.common.PacketSettings;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Set;

/**
 * Command /morphcore
 * 
 * This command allows to manage
 */
public final class CommandMorphCore
{
    private CommandMorphCore()
    {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("morphcore")
            .requires(source -> source.hasPermission(3))
            .then(Commands.literal("reload")
                .then(Commands.literal("blacklist").executes(context -> reloadBlacklist(context)))
                .then(Commands.literal("morphs").executes(context -> reloadMorphs(context)))
                .then(Commands.literal("remapper").executes(context -> reloadRemapper()))));
    }

    private static int reloadBlacklist(CommandContext<CommandSourceStack> context)
    {
        Set<String> blacklist = MorphUtils.reloadBlacklist();
        MorphManager.INSTANCE.setActiveBlacklist(context.getSource().getLevel(), blacklist);
        broadcastPacket(context.getSource().getServer(), new PacketBlacklist(blacklist));

        return 1;
    }

    private static int reloadMorphs(CommandContext<CommandSourceStack> context)
    {
        Map<String, MorphSettings> settings = MorphUtils.reloadMorphSettings();
        MorphManager.INSTANCE.setActiveSettings(settings);
        broadcastPacket(context.getSource().getServer(), new PacketSettings(settings));

        return 1;
    }

    private static int reloadRemapper()
    {
        Map<String, String> map = MorphUtils.reloadRemapper();
        MorphManager.INSTANCE.setActiveMap(map);

        return 1;
    }

    private static void broadcastPacket(MinecraftServer server, IMessage packet)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            Dispatcher.sendTo(packet, player);
        }
    }
}