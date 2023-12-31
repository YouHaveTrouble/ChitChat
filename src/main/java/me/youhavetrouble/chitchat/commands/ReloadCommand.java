package me.youhavetrouble.chitchat.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.youhavetrouble.chitchat.ChitChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReloadCommand {

    public ReloadCommand(ChitChat plugin) {

        new CommandAPICommand("chitchat")
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("chitchat.command.reload")
                        .executes((sender, args) -> {
                            plugin.reloadPluginConfig();
                            sender.sendMessage(Component.text("Reloaded ChitChat", NamedTextColor.GREEN));
                        })
                )
                .register();

    }

}
