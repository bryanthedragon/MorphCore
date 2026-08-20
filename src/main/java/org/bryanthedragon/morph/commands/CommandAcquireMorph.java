package bryanthedragon.morph.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.MorphManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public final class CommandAcquireMorph
{
    private CommandAcquireMorph()
    {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("acquire_morph")
            .requires(source -> source.hasPermission(3))
            .then(Commands.argument("target", EntityArgument.entity())
                .then(Commands.argument("morph", StringArgumentType.word())
                    .executes(CommandAcquireMorph::acquireWithoutNbt)
                    .then(Commands.argument("nbt", StringArgumentType.greedyString())
                        .executes(CommandAcquireMorph::acquireWithNbt)))));
    }

    private static int acquireWithoutNbt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return acquire(context, null);
    }

    private static int acquireWithNbt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        return acquire(context, StringArgumentType.getString(context, "nbt"));
    }

    private static int acquire(CommandContext<CommandSourceStack> context, String nbt) throws CommandSyntaxException
    {
        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, "target");

        if (!(target instanceof Player player))
        {
            source.sendFailure(Component.translatable("morphcore.error.morph.not_player", target.getDisplayName()));

            return 0;
        }

        String morphName = StringArgumentType.getString(context, "morph");
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

        if (!MorphAPI.acquire(player, MorphManager.INSTANCE.morphFromNBT(tag)))
        {
            source.sendFailure(Component.translatable("morphcore.error.acquire", morphName));

            return 0;
        }

        if (source.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK))
        {
            source.sendSuccess(() -> Component.translatable("morphcore.success.acquire", target.getName().getString(), morphName), false);
        }

        return 1;
    }
}