package me.youhavetrouble.chitchat;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.youhavetrouble.chitchat.listeners.ChatListener;
import me.youhavetrouble.chitchat.listeners.CustomClickListener;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();

            commands.register(
                    Commands.literal("chitchat").requires(css -> css.getSender().hasPermission("chitchat.command"))
                            .then(Commands.literal("reload")
                                    .requires(css -> css.getSender().hasPermission("chitchat.command.reload"))
                                    .executes(context -> {
                                        reloadPluginConfig();
                                        context.getSource().getSender().sendMessage(Component.text("Reloaded ChitChat", NamedTextColor.GREEN));
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("removeplayermessages")
                                    .requires(css -> css.getSender().hasPermission("chitchat.command.removeplayermessages"))
                                    .then(Commands.argument("players", ArgumentTypes.playerProfiles())
                                            .requires(css -> css.getSender().hasPermission("chitchat.command.removeplayermessages"))
                                            .executes(context -> {
                                                PlayerProfileListResolver targetResolver = context.getArgument("players", PlayerProfileListResolver.class);
                                                Collection<PlayerProfile> players = targetResolver.resolve(context.getSource());
                                                if (players.isEmpty()) {
                                                    context.getSource().getSender().sendRichMessage("<red>No targets found.");
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                                for (PlayerProfile profile : players) {
                                                    deleteMessages(profile);
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                            .build()
            );
        });

    }

    public void reloadPluginConfig() {
        papiHooked = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        try {
            saveDefaultConfig();
            reloadConfig();
            FileConfiguration config = getConfig();
            this.format = config.getString("format", "<playername>: <message>");

        } catch (Exception e) {
            getLogger().severe("Error loading config: " + e.getMessage());
        }

        HandlerList.unregisterAll(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomClickListener(this), this);
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

    public void deleteMessages(PlayerProfile profile) {
        if (profile == null) return;
        if (profile.getId() == null) return;
        Set<UUID> messages = signatures.getMessagesByPlayerUUID(profile.getId());
        deleteMessages(messages);
        signatures.removeMessagesByPlayerUUID(profile.getId());
    }

}
