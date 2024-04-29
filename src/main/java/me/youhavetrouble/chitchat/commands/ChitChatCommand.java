package me.youhavetrouble.chitchat.commands;

import me.youhavetrouble.chitchat.ChitChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChitChatCommand extends Command {

    private final ChitChat plugin;

    public ChitChatCommand(ChitChat plugin) {
        super("chitchat", "Main ChitChat command", "/chitchat <arg>", new ArrayList<>(0));
        this.plugin = plugin;
        setPermission("chitchat.command.reload");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {

        if (strings.length != 1) {
            commandSender.sendMessage(Component.text(getUsage()));
            return false;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            plugin.reloadPluginConfig();
            commandSender.sendMessage(Component.text("Reloaded ChitChat", NamedTextColor.GREEN));
            return true;
        }

        commandSender.sendMessage(Component.text(getUsage()));
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return org.bukkit.util.StringUtil.copyPartialMatches(args[0], List.of("reload"), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
