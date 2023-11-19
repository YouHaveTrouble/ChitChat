package me.youhavetrouble.chitchat.listeners;


import io.papermc.paper.event.player.AsyncChatEvent;
import me.youhavetrouble.chitchat.ChitChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        event.renderer(ChitChat.getRenderer());
    }

}
