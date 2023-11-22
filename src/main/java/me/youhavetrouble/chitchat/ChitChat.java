package me.youhavetrouble.chitchat;

import me.youhavetrouble.chitchat.commands.RemoveMessageCommand;
import me.youhavetrouble.chitchat.listeners.ChatListener;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ChitChat extends JavaPlugin {

    private boolean papiHooked = false;

    private SignatureCache signatures;

    private String format;

    @Override
    public void onEnable() {
        reloadPluginConfig();
        this.signatures = new SignatureCache(getConfig().getInt("signature-cache-size", 1000));
        new RemoveMessageCommand(this);
    }

    public void reloadPluginConfig() {

        papiHooked = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        try {
            saveDefaultConfig();
            FileConfiguration config = getConfig();
            this.format = config.getString("format");

        } catch (Exception e) {
            getLogger().severe("Error loading config: " + e.getMessage());
        }



        HandlerList.unregisterAll(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }


    public boolean isPapiHooked() {
        return papiHooked;
    }

    public String getFormat() {
        return format;
    }

    @Nullable
    public SignedMessage.Signature getCachedSignature(UUID uuid) {
        return signatures.get(uuid);
    }

    public UUID cacheSignature(SignedMessage.Signature signature) {
        if (signature == null) return null;

        UUID id = UUID.nameUUIDFromBytes(signature.bytes());
        if (signatures.containsKey(id)) {
            while (signatures.containsKey(id)) {
                id = UUID.randomUUID();
            }
        }
        signatures.put(id, signature);
        return id;
    }

}
