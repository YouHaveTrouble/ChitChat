package me.youhavetrouble.chitchat.commands;

import me.youhavetrouble.chitchat.ChitChat;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class RemoveMessageCommand extends Command {

    private final ChitChat plugin;

    public RemoveMessageCommand(ChitChat plugin) {
        super(
                "deletemessage",
                "removes a message with provided signature id",
                "/deletemessage <signature_id>",
                new ArrayList<>()
        );
        this.plugin = plugin;
        plugin.getServer().getCommandMap().register("chitchat", this);
        setPermission("chitchat.deletemessage");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (args.length != 1) {
            sender.sendMessage(Component.text(getUsage(), NamedTextColor.RED));
            return false;
        }

        String rawSignatureId = args[0];
        UUID signatureId;
        try {
            signatureId = UUID.fromString(rawSignatureId);
            SignedMessage.Signature signature = plugin.getCachedSignature(signatureId);
            if (signature == null) {
                sender.sendMessage(Component.text("No message with that id found", NamedTextColor.RED));
                return false;
            }
            plugin.getServer().deleteMessage(signature);
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid message id", NamedTextColor.RED));
            return false;
        }
    }
}
