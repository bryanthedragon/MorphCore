package bryanthedragon.morph.commands.config;

import bryanthedragon.morph.config.values.Value;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class SubCommandConfigPrint
{
    private SubCommandConfigPrint()
    {}

    public static int execute(CommandContext<CommandSourceStack> context)
    {
        String id = StringArgumentType.getString(context, "id");
        Value value = SubCommandConfigBase.getValue(id);

        if (value == null)
        {
            context.getSource().sendFailure(Component.translatable("morphcore.error.config.invalid_id", id));

            return 0;
        }

        if (!value.isClientSide())
        {
            SubCommandConfigBase.sendInfo(context.getSource(), "print", id, value.toString());
        }
        else
        {
            SubCommandConfigBase.sendInfo(context.getSource(), "client_side", id);
        }

        return 1;
    }
}
