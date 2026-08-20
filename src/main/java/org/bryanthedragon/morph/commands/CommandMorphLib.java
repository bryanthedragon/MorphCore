package bryanthedragon.morph.commands;

import bryanthedragon.morph.commands.config.SubCommandConfig;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public final class CommandMorphLib
{
    private CommandMorphLib()
    {
        
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        SubCommandConfig.register(dispatcher);
    }
}
