package me.vesder.blazeyChat.configs.customconfigs;

import lombok.Getter;
import me.vesder.blazeyChat.configs.ConfigUtils;
import me.vesder.blazeyChat.configs.CustomConfig;
import org.bukkit.configuration.ConfigurationSection;

public class FormatConfig extends CustomConfig {

    @Getter
    private ConfigurationSection formatSection;

    @Override
    public String getName() {
        return "format.yml";
    }

    @Override
    public void loadValues() {
        formatSection = ConfigUtils.getConfigSection("format.yml", "group-formats");
    }
}
