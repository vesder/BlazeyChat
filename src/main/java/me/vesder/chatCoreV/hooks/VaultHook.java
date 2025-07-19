package me.vesder.chatCoreV.hooks;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultHook {

    @Getter
    private static Economy econ = null;
    @Getter
    private static Permission perms = null;
    @Getter
    private static Chat chat = null;

    private VaultHook() {}

    private static void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) {
            econ = rsp.getProvider();
        }
    }

    private static void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);

        if (rsp != null) {
            chat = rsp.getProvider();
        }
    }

    private static void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);

        if (rsp != null) {
            perms = rsp.getProvider();
        }
    }

    public static boolean hasEconomy() {
        return econ != null;
    }

    public static boolean hasPermissions() {
        return perms != null;
    }

    public static boolean hasChat() {
        return chat != null;
    }

    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
            setupChat();
            setupPermissions();
        } else {
            Bukkit.getLogger().info("Vault not found! Some features may not work.");
        }
    }

}
