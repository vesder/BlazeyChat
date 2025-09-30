package me.vesder.blazeyChat.commands.subcommands;

import me.vesder.blazeyChat.commands.SubCommand;
import me.vesder.blazeyChat.data.User;
import me.vesder.blazeyChat.data.UserManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.vesder.blazeyChat.commands.CommandManager.sendHelpMessage;

public class AdsCommand implements SubCommand {

    @Override
    public String getName() {
        return "ads";
    }

    @Override
    public String getDescription() {
        return "Broadcast ads and customize related settings.";
    }

    @Override
    public String getSyntax() {
        return "/ccv ads [settings]";
    }

    @Override
    public String getPermission() {
        return "chatcorev.command.ads";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (args.length >= 4) {

            if (args[2].equals("sound")) {
                User user = UserManager.getUser(player.getUniqueId());
                user.setAdsMusic(args[3]);
                player.sendMessage("ADS SOUND SUCCESSFULLY SET TO : " + user.getAdsMusic());
                return;
            }

        }

        sendHelpMessage(player, getName());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        if (args.length == 2) {

            return List.of(
                "setting",
                "message"
            );
        }


        if (args.length == 3 && args[1].equals("setting")) {

            return List.of("sound");
        }

        if (args.length == 4 && args[1].equals("setting") && args[2].equals("sound")) {

            List<String> sounds = new ArrayList<>();
            for (Sound sound : Sound.values()) {
                sounds.add(sound.name());
            }
            return sounds;
        }

        return List.of();
    }
}
