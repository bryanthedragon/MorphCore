package bryanthedragon.morph.commands;

import java.util.List;

import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Command /morph
 * 
 * This command is responsible for morphing a player into given morph.
 */
public class CommandMorph extends CommandBase
{
    @Override
    public String getName()
    {
        return "morph";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "morphcore.commands.morph";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender instanceof CommandBlockBaseLogic || super.checkPermission(server, sender);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        Entity entity = getEntity(server, sender, args[0]);

        if (!(entity instanceof Player))
        {
            throw new CommandException("morphcore.error.morph.not_player", entity.getDisplayName());
        }

        Player player = (Player) entity;

        if (args.length < 2)
        {
            MorphAPI.demorph(player);

            if (sender.sendCommandFeedback())
            {
                sender.sendMessage(new TextComponentTranslation("morphcore.success.demorph", args[0]));
            }
        }
        else
        {
            CompoundTag tag = null;
            String mergedTagArgs = "";

            if (args.length >= 3)
            {
                try
                {
                    mergedTagArgs = mergeArgs(args, 2);
                    tag = JsonToNBT.getTagFromJson(mergedTagArgs);
                }
                catch (Exception e)
                {
                    throw new CommandException("morphcore.error.morph.nbt", e.getMessage());
                }
            }

            if (tag == null)
            {
                tag = new CompoundTag();
            }

            tag.setString("Name", args[1]);

            AbstractMorph newMorph = MorphManager.INSTANCE.morphFromNBT(tag);
            boolean morphFound = newMorph != null;
            
            if (!morphFound)
            {
                throw new CommandException("morphcore.error.morph.factory", args[0], args[1], mergedTagArgs);
            }
            
            MorphAPI.morph(player, newMorph, true);

            if (sender.sendCommandFeedback())
            {
                sender.sendMessage(new TextComponentTranslation("morphcore.success.morph", args[0], args[1]));
            }
        }
    }

    /**
     * Provide completion for player usernames for first argument
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }

        return super.getTabCompletions(server, sender, args, pos);
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