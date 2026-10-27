package me.youhavetrouble.chitchat.listeners;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import me.youhavetrouble.chitchat.ChitChat;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CustomClickListener implements Listener {

    private final ChitChat plugin;

    public CustomClickListener(ChitChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerCustomAction(PlayerCustomClickEvent event) {
        if (!(event.getCommonConnection() instanceof PlayerGameConnection connection)) return;
        Key key = event.getIdentifier();
        if (!key.namespace().equals("chitchat-delete")) return;
        if (!connection.getPlayer().hasPermission("chitchat.deletemessage")) return;

        UUID messageId = null;
        try {
            messageId = UUID.fromString(key.value());
        } catch (IllegalArgumentException ignored) {
        }
        plugin.deleteMessages(messageId);
    }

}
