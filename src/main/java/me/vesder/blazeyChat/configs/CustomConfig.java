package me.vesder.blazeyChat.configs;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class CustomConfig {

    public File file;
    public YamlConfiguration config;

    public abstract String getName();

    public void loadValues() {}

}
