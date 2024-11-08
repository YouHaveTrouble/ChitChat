package me.youhavetrouble.chitchat;

import me.youhavetrouble.chitchat.commands.ChitChatCommand;
import me.youhavetrouble.chitchat.commands.RemoveMessageCommand;
import me.youhavetrouble.chitchat.commands.RemovePlayersMessagesCommand;
import me.youhavetrouble.chitchat.listeners.ChatListener;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public final class ChitChat extends JavaPlugin {

    private boolean papiHooked = false;

    private SignatureCache signatures;

    private String format;

    @Override
    public void onEnable() {
        reloadPluginConfig();
        this.signatures = new SignatureCache(getConfig().getInt("signature-cache-size", 1000));

        getServer().getCommandMap().register("chitchat", new ChitChatCommand(this));
        getServer().getCommandMap().register("chitchat", new RemoveMessageCommand(this));
        getServer().getCommandMap().register("chitchat", new RemovePlayersMessagesCommand(this));

        new RemoveMessageCommand(this);
        new ChitChatCommand(this);
    }

    public void reloadPluginConfig() {

        papiHooked = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        try {
            saveDefaultConfig();
            reloadConfig();
            FileConfiguration config = getConfig();
            this.format = config.getString("format","<playername>: <message>");

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
    public MessageData getCachedSignature(UUID uuid) {
        return signatures.get(uuid);
    }

    public UUID cacheSignature(@NotNull SignedMessage signedMessage, Player player) {
        SignedMessage.Signature signature = signedMessage.signature();
        if (signature == null) return null;

        UUID id = UUID.nameUUIDFromBytes(signature.bytes());
        if (signatures.containsKey(id)) {
            while (signatures.containsKey(id)) {
                id = UUID.randomUUID();
            }
        }
        signatures.put(id, new MessageData(signedMessage, id, player.getUniqueId()));
        return id;
    }

    public void deleteMessages(UUID... messageIds) {
        for (UUID messageId : messageIds) {
            MessageData messageData = signatures.get(messageId);
            if (messageData == null) continue;
            if (!messageData.signedMessage().canDelete()) return;
            if (messageData.signedMessage().signature() == null) return;
            getServer().deleteMessage(messageData.signedMessage().signature());
            signatures.remove(messageId);
        }
    }

    public void deleteMessages(@NotNull Collection<UUID> messageIds) {
        for (UUID messageId : messageIds) {
            MessageData messageData = signatures.get(messageId);
            if (messageData == null) continue;
            if (!messageData.signedMessage().canDelete()) return;
            if (messageData.signedMessage().signature() == null) return;
            getServer().deleteMessage(messageData.signedMessage().signature());
            signatures.remove(messageId);
        }
    }

    public void deleteMessages(OfflinePlayer offlinePlayer) {
        Set<UUID> messages = signatures.getMessagesByPlayerUUID(offlinePlayer.getUniqueId());
        deleteMessages(messages);
        signatures.removeMessagesByPlayerUUID(offlinePlayer.getUniqueId());
    }

}
