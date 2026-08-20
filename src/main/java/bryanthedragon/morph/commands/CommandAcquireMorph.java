package bryanthedragon.morph.commands;

import java.util.List;

import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.MorphManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Command /acquire_morph
 * 
 * This command is responsible for sending a morph to given player as an 
 * acquired morph.
 */
public class CommandAcquireMorph extends CommandSourceStack
{
    public CommandAcquireMorph(CommandSource p_81302_, Vec3 p_81303_, Vec2 p_81304_, ServerLevel p_81305_, int p_81306_, String p_81307_, Component p_81308_, MinecraftServer p_81309_, Entity p_81310_) {
        super(p_81302_, p_81303_, p_81304_, p_81305_, p_81306_, p_81307_, p_81308_, p_81309_, p_81310_);
        //TODO Auto-generated constructor stub
    }

    public String getName()
    {
        return "acquire_morph";
    }

    public String getUsage(CommandSourceStack sender)
    {
        return "morphcore.commands.acquire_morph";
    }

    public int getRequiredPermissionLevel()
    {
        /* Because /op command has the same level, and I trust it */
        return 3;
    }

    public boolean checkPermission(CommandSourceStack sender)
    {
        return sender.hasPermission(getRequiredPermissionLevel());
    }

    public void execute(MinecraftServer server, CommandSourceStack sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        Entity entity = getEntity(server, sender, args[0]);

        if (!(entity instanceof Player))
        {
            throw new CommandException("morphcore.error.morph.not_player", entity.getDisplayName());
        }

        ServerPlayer player = (ServerPlayer) entity;

        CompoundTag tag = null;

        if (args.length >= 3)
        {
            try
            {
                tag = JsonToNBT.getTagFromJson(CommandMorph.mergeArgs(args, 2));
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

        tag.putString("Name", args[1]);

        if (!MorphAPI.acquire(player, MorphManager.INSTANCE.morphFromNBT(tag)))
        {
            throw new CommandException("morphcore.error.acquire", args[1]);
        }

        if (sender.sendCommandFeedback())
        {
            sender.sendMessage(new TextComponentTranslation("morphcore.success.acquire", args[0], args[1]));
        }
    }

    /**
     * Provide completion for player usernames for first argument
     */
    public List<String> getTabCompletions(MinecraftServer server, CommandSourceStack sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getPlayerNames());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}