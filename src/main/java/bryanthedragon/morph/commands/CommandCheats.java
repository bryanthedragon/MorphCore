package bryanthedragon.morph.commands;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.commands.utils.L10n;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.List;

public class CommandCheats extends McCommandBase
{
    public L10n getL10n()
    {
        return MorphLib.l10n;
    }

    public String getName()
    {
        return "cheats";
    }

    public String getUsage(ICommandSender sender)
    {
        return "morphlib.commands.cheats";
    }

    public String getSyntax()
    {
        return "{l}{6}/{r}cheats {7}<enabled:true|false>{r}";
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredArgs()
    {
        return 1;
    }

    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        sender.getEntityWorld().getWorldInfo().setAllowCommands(CommandBase.parseBoolean(args[0]));
        server.saveAllWorlds(false);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, McCommandBase.BOOLEANS);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
