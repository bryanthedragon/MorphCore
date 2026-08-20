package bryanthedragon.morph.commands.config;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.commands.SubCommandBase;
import bryanthedragon.morph.commands.utils.L10n;
import bryanthedragon.morph.config.values.Value;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class SubCommandConfig extends SubCommandBase
{
    public static Value get(String id) throws CommandException
    {
        try
        {
            String[] splits = id.split("\\.");

            if (splits.length != 3)
            {
                throw new Exception("Identifier should have exactly 3 strings separated by a period!");
            }

            return MorphLib.proxy.configs.modules.get(splits[0]).values.get(splits[1]).getSubValue(splits[2]);
        }
        catch (Exception e)
        {
            throw new CommandException("config.invalid_id", id);
        }
    }

    public SubCommandConfig()
    {
        this.add(new SubCommandConfigPrint());
        this.add(new SubCommandConfigSet());
    }

    @Override
    public String getName()
    {
        return "config";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "morphlib.commands.morphlib.config.help";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}morphlib {8}config{r}";
    }

    @Override
    public L10n getL10n()
    {
        return MorphLib.l10n;
    }
}
