package me.youhavetrouble.chitchat;

import io.papermc.paper.chat.ChatRenderer;
import me.youhavetrouble.chitchat.listeners.ChatListener;
import me.youhavetrouble.chitchat.renderer.ChitChatRenderer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChitChat extends JavaPlugin {

    private static ChatRenderer renderer;

    @Override
    public void onEnable() {
        reloadPluginConfig();
    }

    public static ChatRenderer getRenderer() {
        return renderer;
    }

    public void reloadPluginConfig() {

        boolean papiHooked = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        try {
            saveDefaultConfig();
            FileConfiguration config = getConfig();

            renderer = new ChitChatRenderer(
                    config.getString("format", "<playername>: <message>"),
                    papiHooked
            );

        } catch (Exception e) {
            getLogger().severe("Error loading config: " + e.getMessage());
        }

        HandlerList.unregisterAll(this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

}
