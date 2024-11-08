package me.youhavetrouble.chitchat.commands;

import me.youhavetrouble.chitchat.ChitChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RemovePlayersMessagesCommand extends Command {

    private final ChitChat plugin;

    public RemovePlayersMessagesCommand(ChitChat plugin) {
        super("deleteplayermessages", "Deletes message with given id", "/deleteplayermessages <player_name>", new ArrayList<>());
        setPermission("chitchat.deleteplayermessages");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {

        if (strings.length != 1) {
            commandSender.sendMessage(Component.text(getUsage()));
            return false;
        }

        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayerIfCached(strings[0]);
        if (offlinePlayer == null) {
            commandSender.sendMessage(Component.text("No player with that name found", NamedTextColor.RED));
            return true;
        }
        plugin.deleteMessages(offlinePlayer);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            if (sender instanceof Player playerSender) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.isInvisible()) continue;
                    if (!playerSender.canSee(player)) continue;
                    playerNames.add(player.getName());
                }
            } else {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.isInvisible()) continue;
                    playerNames.add(player.getName());
                }
            }
            return org.bukkit.util.StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>());
        }
        return new ArrayList<>();
    }

}
