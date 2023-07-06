package com.tomo.thereisway.management.utilities;
import org.bukkit.ChatColor;

public class ChatUtils {

    private static String chat(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String asRedMessage(String text) {
        return coloredMessage(text, ChatColor.RED);
    }

    public static String asGreenMessage(String text) {
        return coloredMessage(text, ChatColor.GREEN);
    }

    public static String asBlueMessage(String text) {
        return coloredMessage(text, ChatColor.BLUE);
    }

    public static String asYellowMessage(String text) {
        return coloredMessage(text, ChatColor.YELLOW);
    }

    public static String asGoldMessage(String text) {
        return coloredMessage(text, ChatColor.GOLD);
    }

    public static String withoutColor(String text) {
        return ChatColor.stripColor(text);
    }

    public static String asDarkPurpleMessage(String text) {
        return coloredMessage(text, ChatColor.DARK_PURPLE);
    }

    public static String coloredMessage(String text, ChatColor color) {
        return chat(color + text);
    }
}
