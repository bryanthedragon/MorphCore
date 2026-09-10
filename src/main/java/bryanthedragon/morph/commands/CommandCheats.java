package bryanthedragon.morph.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;

import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * Cheats command
 *
 * Toggles "allow cheats" of a single player world ({@code /cheats <true|false>}).
 * Vanilla exposes no setter for {@link LevelSettings#allowCommands()}, so the
 * level's settings record is rebuilt and swapped in via reflection.
 */
public class CommandCheats
{
    /** SRG name of {@link PrimaryLevelData}'s {@code settings} field */
    private static final String SETTINGS_FIELD = "f_78443_";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("cheats")
            .then(Commands.argument("enabled", BoolArgumentType.bool())
                .executes(CommandCheats::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context)
    {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        MinecraftServer server = context.getSource().getServer();

        if (!(server.getWorldData() instanceof PrimaryLevelData data))
        {
            context.getSource().sendFailure(Component.translatable("morphlib.commands.cheats"));

            return 0;
        }

        LevelSettings old = data.getLevelSettings();
        LevelSettings settings = new LevelSettings(old.levelName(), old.gameType(), old.hardcore(), old.difficulty(), enabled, old.gameRules(), old.getDataConfiguration());

        ObfuscationReflectionHelper.setPrivateValue(PrimaryLevelData.class, data, settings, SETTINGS_FIELD);
        server.saveEverything(true, false, false);

        context.getSource().sendSuccess(() -> Component.translatable("morphlib.commands.cheats"), true);

        return 1;
    }
}
