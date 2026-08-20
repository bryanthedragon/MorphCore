package bryanthedragon.morph.commands.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Localization utils
 *
 * This class provides shortcuts for sending messages to players. Pretty tired
 * of typing a lot of characters with provided API.
 *
 * API should be clear, short and concise.
 */
public class L10n
{
    public static String ERROR_MARKER = "§4(§cX§4)§r ";
    public static String SUCCESS_MARKER = "§2(§aV§2)§r ";
    public static String INFO_MARKER = "§9(§bi§9)§r ";

    private final String id;

    public L10n(String id)
    {
        this.id = id;
    }

    /**
     * Send a translated message to player
     */
    public void send(CommandSourceStack sender, String key, Object... objects)
    {
        sender.sendSystemMessage(Component.translatable(key, objects));
    }

    /**
     * Send a translated message to player
     */
    public void sendColored(CommandSourceStack sender, ChatFormatting color, String key, Object... objects)
    {
        sender.sendSystemMessage(Component.translatable(key, objects).withStyle(color));
    }

    /**
     * Send error message to the sender
     */
    public void error(CommandSourceStack sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, ERROR_MARKER, this.id + ".error." + key, objects);
    }

    /**
     * Get error message
     */
    public Component error(String key, Object... objects)
    {
        return this.messageWithMarker(ERROR_MARKER, this.id + ".error." + key, objects);
    }

    /**
     * Send success message to the sender
     */
    public void success(CommandSourceStack sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, SUCCESS_MARKER, this.id + ".success." + key, objects);
    }

    /**
     * Get success message
     */
    public Component success(String key, Object... objects)
    {
        return this.messageWithMarker(SUCCESS_MARKER, this.id + ".success." + key, objects);
    }

    /**
     * Send informing message to the sender
     */
    public void info(CommandSourceStack sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, INFO_MARKER, this.id + ".info." + key, objects);
    }

    /**
     * Get informing message
     */
    public Component info(String key, Object... objects)
    {
        return  this.messageWithMarker(INFO_MARKER, this.id + ".info." + key, objects);
    }

    /**
     * Send a message with given marker
     */
    public void sendWithMarker(CommandSourceStack sender, String marker, String key, Object... objects)
    {
        sender.sendSystemMessage(this.messageWithMarker(marker, key, objects));
    }

    public Component messageWithMarker(String marker, String key, Object... objects)
    {
        return Component.literal(marker)
            .append(Component.translatable(key, objects).withStyle(ChatFormatting.GRAY));
    }
}