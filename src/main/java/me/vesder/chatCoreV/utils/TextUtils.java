package me.vesder.chatCoreV.utils;

public class TextUtils {

    public static String parseLegacyColorCodes(String text) {

        text = text.replace("&0", "<black>");
        text = text.replace("&1", "<dark_blue>");
        text = text.replace("&2", "<dark_green>");
        text = text.replace("&3", "<dark_aqua>");
        text = text.replace("&4", "<dark_red>");
        text = text.replace("&5", "<dark_purple>");
        text = text.replace("&6", "<gold>");
        text = text.replace("&7", "<gray>");
        text = text.replace("&8", "<dark_gray>");
        text = text.replace("&9", "<blue>");
        text = text.replace("&a", "<green>");
        text = text.replace("&b", "<aqua>");
        text = text.replace("&c", "<red>");
        text = text.replace("&d", "<light_purple>");
        text = text.replace("&e", "<yellow>");
        text = text.replace("&f", "<white>");

        text = text.replace("&l", "<bold>");
        text = text.replace("&o", "<italic>");
        text = text.replace("&n", "<underlined>");
        text = text.replace("&m", "<strikethrough>");
        text = text.replace("&k", "<obfuscated>");
        text = text.replace("&r", "<reset>");

        return text;
    }

}
