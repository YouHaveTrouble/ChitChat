package me.youhavetrouble.chitchat.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.UUIDArgument;
import me.youhavetrouble.chitchat.ChitChat;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class RemoveMessageCommand {

    public RemoveMessageCommand(ChitChat plugin) {

        new CommandAPICommand("deletemessage")
                .withPermission("chitchat.deletemessage")
                .withArguments(new UUIDArgument("signature_id"))
                .executes((sender, args) -> {
                    UUID signatureId = (UUID) args.get("signature_id");
                    if (signatureId == null) return;
                    SignedMessage.Signature signature = plugin.getCachedSignature(signatureId);
                    if (signature == null) {
                        sender.sendMessage(Component.text("No message with that id found", NamedTextColor.RED));
                        return;
                    }
                    plugin.getServer().deleteMessage(signature);
                    plugin.removeCachedSignature(signatureId);
                })
                .register();

    }

}
