package bryanthedragon.morph.commands.config;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.config.Config;
import bryanthedragon.morph.config.values.Value;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SubCommandConfigBase
{
    private SubCommandConfigBase()
    {}

    public static CompletableFuture<Suggestions> suggestValues(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder)
    {
        List<String> ids = new ArrayList<>();

        for (Config config : MorphLib.proxy.configs.modules.values())
        {
            for (Value category : config.values.values())
            {
                for (Value value : category.getSubValues())
                {
                    if (!value.isClientSide())
                    {
                        ids.add(config.id + "." + category.id + "." + value.id);
                    }
                }
            }
        }

        return SharedSuggestionProvider.suggest(ids, builder);
    }

    public static Value getValue(String id)
    {
        String[] parts = id.split("\\.", -1);

        if (parts.length != 3)
        {
            return null;
        }

        Config config = MorphLib.proxy.configs.modules.get(parts[0]);

        return config == null ? null : config.get(parts[1], parts[2]);
    }

    public static void sendInfo(CommandSourceStack source, String key, Object... args)
    {
        source.sendSuccess(() -> Component.literal("§9(§bi§9)§r ").append(Component.translatable("morphcore.info.config." + key, args).withStyle(ChatFormatting.GRAY)), false);
    }
}
