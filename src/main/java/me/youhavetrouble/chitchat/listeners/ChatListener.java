package me.youhavetrouble.chitchat.listeners;


import io.papermc.paper.event.player.AsyncChatEvent;
import me.youhavetrouble.chitchat.ChitChat;
import me.youhavetrouble.chitchat.renderer.ChitChatRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class ChatListener implements Listener {

    private final ChitChat plugin;

    public ChatListener(ChitChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        event.renderer(new ChitChatRenderer(plugin, plugin.getFormat(), event.signedMessage()));
    }

}
