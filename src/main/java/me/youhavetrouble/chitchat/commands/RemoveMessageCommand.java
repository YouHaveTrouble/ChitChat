package me.youhavetrouble.chitchat.commands;

import me.youhavetrouble.chitchat.ChitChat;
import me.youhavetrouble.chitchat.MessageData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RemoveMessageCommand extends Command {

    private final ChitChat plugin;

    public RemoveMessageCommand(ChitChat plugin) {
        super("deletemessage", "Deletes message with given id", "/deletemessage <signature_id>", new ArrayList<>());
        setPermission("chitchat.deletemessage");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {

        if (strings.length != 1) {
            commandSender.sendMessage(Component.text(getUsage()));
            return false;
        }

        try {
            UUID signatureId = UUID.fromString(strings[0]);
            MessageData messageData = plugin.getCachedSignature(signatureId);
            if (messageData == null) {
                commandSender.sendMessage(Component.text("No message with that id found", NamedTextColor.RED));
                return true;
            }
            plugin.deleteMessages(signatureId);
            return true;
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(Component.text("Invalid message id", NamedTextColor.RED));
            return true;
        }

    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
