package bryanthedragon.morph.commands.config;

import bryanthedragon.morph.config.values.Value;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SubCommandConfigPrint extends SubCommandConfigBase
{
    @Override
    public String getName()
    {
        return "print";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "morphlib.commands.morphlib.config.print";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}morphlib {8}config print{r} {7}<mod.category.option>{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Value value = SubCommandConfig.get(args[0]);

        if (!value.isClientSide())
        {
            this.getL10n().info(sender, "config.print", args[0], value.toString());
        }
        else
        {
            this.getL10n().info(sender, "config.client_side", args[0]);
        }
    }
}
