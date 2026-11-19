package org.bryanthedragon.morph.commands.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.config.Config;
import org.bryanthedragon.morph.config.ConfigManager;
import org.bryanthedragon.morph.config.values.IServerValue;
import org.bryanthedragon.morph.config.values.Value;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class SubCommandConfigSet
{
    private SubCommandConfigSet()
    {}

    public static int execute(CommandContext<CommandSourceStack> context)
    {
        String id = StringArgumentType.getString(context, "id");
        String input = StringArgumentType.getString(context, "value");
        Value value = SubCommandConfigBase.getValue(id);

        if (value == null)
        {
            context.getSource().sendFailure(Component.translatable("morphcore.error.config.invalid_id", id));

            return 0;
        }

        if (!value.isClientSide() && value instanceof IServerValue serverValue)
        {
            if (!serverValue.parseFromCommand(input))
            {
                context.getSource().sendFailure(Component.translatable("morphcore.error.config.invalid_format", id, input));

                return 0;
            }

            if (value.isSyncable())
            {
                String moduleId = id.substring(0, id.indexOf('.'));
                Config config = MorphLib.proxy.configs.modules.get(moduleId);

                ConfigManager.synchronizeConfig(config.filterSyncable(), context.getSource().getServer());
            }

            SubCommandConfigBase.sendInfo(context.getSource(), "set", id, value.toString());
        }
        else
        {
            SubCommandConfigBase.sendInfo(context.getSource(), "client_side", id);
        }

        return 1;
    }
}
