package bryanthedragon.morph.commands.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class SubCommandConfig
{
    private SubCommandConfig()
    {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("morphlib").then(Commands.literal("config").then(Commands.literal("print").then(Commands.argument("id", StringArgumentType.word()).suggests(SubCommandConfigBase::suggestValues).executes(SubCommandConfigPrint::execute))).then(Commands.literal("set").then(Commands.argument("id", StringArgumentType.word()).suggests(SubCommandConfigBase::suggestValues).then(Commands.argument("value", StringArgumentType.greedyString()).executes(SubCommandConfigSet::execute))))));
    }
}
