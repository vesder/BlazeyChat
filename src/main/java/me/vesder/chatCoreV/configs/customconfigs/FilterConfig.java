package me.vesder.chatCoreV.configs.customconfigs;

import lombok.Getter;
import me.vesder.chatCoreV.configs.ConfigUtils;
import me.vesder.chatCoreV.configs.CustomConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class FilterConfig extends CustomConfig {

    // Replace Mode
    @Getter
    private List<String> replaceActions;
    @Getter
    private ConfigurationSection replaceWordsSection;
    // Censor Mode
    @Getter
    private char censorChar;
    @Getter
    private List<String> censorActions;
    @Getter
    private List<String> censorWords;
    // Block Mode
    @Getter
    private List<String> blockActions;
    @Getter
    private List<String> blockWords;


    @Override
    public String getName() {
        return "filter.yml";
    }

    @Override
    public void loadValues() {
        // Replace Mode
        replaceActions = ConfigUtils.getStringListConfig(getName(), "replace-mode.actions");
        replaceWordsSection = ConfigUtils.getConfigSection(getName(), "replace-mode.words");
        // Censor Mode
        censorChar = ConfigUtils.getStringConfig(getName(), "censor-mode.character").charAt(0);
        censorActions = ConfigUtils.getStringListConfig(getName(), "censor-mode.actions");
        censorWords = ConfigUtils.getStringListConfig(getName(), "censor-mode.words");
        // Block Mode
        blockActions = ConfigUtils.getStringListConfig(getName(), "block-mode.actions");
        blockWords = ConfigUtils.getStringListConfig(getName(), "block-mode.words");
    }
}
