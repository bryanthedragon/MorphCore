package org.bryanthedragon.morph.commands;

import org.bryanthedragon.morph.api.MorphAPI;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

/**
 * Command /morph
 * 
 * This command is responsible for morphing a player into given morph.
 */
public final class CommandMorph
{
    private CommandMorph()
    {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("morph").requires(source -> source.hasPermission(2)).then(Commands.argument("target", EntityArgument.entity()).executes(CommandMorph::demorph).then(Commands.argument("morph", StringArgumentType.word()).executes(CommandMorph::morphWithoutNbt).then(Commands.argument("nbt", StringArgumentType.greedyString()).executes(CommandMorph::morphWithNbt)))));
    }

    private static int demorph(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, "target");

        if (!(target instanceof Player player))
        {
            source.sendFailure(Component.translatable("morphcore.error.morph.not_player", target.getDisplayName()));

            return 0;
        }

        MorphAPI.demorph(player);

        if (shouldSendFeedback(source))
        {
            source.sendSuccess(() -> Component.translatable("morphcore.success.demorph", target.getName().getString()), false);
        }

        return 1;
    }

    private static int morphWithoutNbt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return morph(context, null);
    }

    private static int morphWithNbt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return morph(context, StringArgumentType.getString(context, "nbt"));
    }

    private static int morph(CommandContext<CommandSourceStack> context, String nbt) throws CommandSyntaxException
    {
        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, "target");

        if (!(target instanceof Player player))
        {
            source.sendFailure(Component.translatable("morphcore.error.morph.not_player", target.getDisplayName()));

            return 0;
        }

        String morphName = StringArgumentType.getString(context, "morph");
        String mergedTagArgs = nbt == null ? "" : nbt;
        CompoundTag tag;

        try
        {
            tag = nbt == null ? new CompoundTag() : TagParser.parseTag(nbt);
        }
        catch (CommandSyntaxException e)
        {
            source.sendFailure(Component.translatable("morphcore.error.morph.nbt", e.getMessage()));

            return 0;
        }

        tag.putString("Name", morphName);

        AbstractMorph newMorph = MorphManager.INSTANCE.morphFromNBT(tag);

        if (newMorph == null)
        {
            source.sendFailure(Component.translatable("morphcore.error.morph.factory", target.getName().getString(), morphName, mergedTagArgs));

            return 0;
        }

        MorphAPI.morph(player, newMorph, true);

        if (shouldSendFeedback(source))
        {
            source.sendSuccess(() -> Component.translatable("morphcore.success.morph", target.getName().getString(), morphName), false);
        }

        return 1;
    }

    private static boolean shouldSendFeedback(CommandSourceStack source)
    {
        return source.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
    }

    /**
     * Merge given args from given index
     * 
     * Basically fold back string array argument back into string from given 
     * index.
     */
    public static String mergeArgs(String[] args, int i)
    {
        String dataTag = "";

        for (; i < args.length; i++)
        {
            dataTag += args[i] + (i == args.length - 1 ? "" : " ");
        }

        return dataTag;
    }
}