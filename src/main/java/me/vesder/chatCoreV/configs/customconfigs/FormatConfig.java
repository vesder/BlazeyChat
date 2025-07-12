package me.vesder.chatCoreV.configs.customconfigs;

import lombok.Getter;
import me.vesder.chatCoreV.configs.ConfigUtils;
import me.vesder.chatCoreV.configs.CustomConfig;
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
