package net.copokbl.worldresetgame;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Util.t("&cOnly players can execute this command."));
            return false;
        }

        switch (args[0]) {
            case "start" -> {
                if (Main.getInstance().getCurrentResetter() != null) {
                    sender.sendMessage(Util.t("&cA world reset game is already in progress."));
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(Util.t("&cPlease specify the time in seconds."));
                    return false;
                }

                int timeSeconds;
                try {
                    timeSeconds = Integer.parseInt(args[1]);
                    if (timeSeconds <= 0) {
                        sender.sendMessage(Util.t("&cTime must be a positive integer."));
                        return false;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Util.t("&cInvalid time format. Please enter a valid integer."));
                    return false;
                }

                World currentWorld = player.getWorld();
                WorldResetter resetter = new WorldResetter(currentWorld, timeSeconds);
                Main.getInstance().setCurrentResetter(resetter);
            }

            case "stop" -> sender.sendMessage(Util.t("&cNot supported yet."));
            default -> sendHelp(sender);
        }

        return false;
    }

    private static void sendHelp(CommandSender sender) {
        sender.sendMessage(Util.t("&6&lWorld Reset Game"));
        sender.sendMessage(Util.t("&e/worldreset start <timeSeconds> &7- Start the world reset game"));
        sender.sendMessage(Util.t("&e/worldreset stop &7- Stop the world reset game"));
    }
}
