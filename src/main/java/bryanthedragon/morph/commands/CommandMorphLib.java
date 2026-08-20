package bryanthedragon.morph.commands;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.commands.config.SubCommandConfig;
import bryanthedragon.morph.commands.utils.L10n;

public class CommandMorphLib extends SubCommandBase
{
    public CommandMorphLib()
    {
        this.add(new SubCommandConfig());
    }

    public String getName()
    {
        return "MorphLib";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "MorphLib.commands.MorphLib.help";
    }

    @Override
    public String getSyntax()
    {
        return "";
    }

    @Override
    public L10n getL10n()
    {
        return MorphLib.l10n;
    }
}
