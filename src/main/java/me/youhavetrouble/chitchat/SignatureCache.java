package me.youhavetrouble.chitchat;

import java.util.*;

public class SignatureCache extends LinkedHashMap<UUID, MessageData> {

    private final int maxSize;

    private final HashMap<UUID, Set<UUID>> playerMessages = new HashMap<>();

    protected SignatureCache(int maxSize) {
        super(maxSize, 1, false);
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxSize;
    }

    public MessageData put(UUID key, MessageData value) {
        if (!playerMessages.containsKey(value.playerId())) {
            playerMessages.put(value.playerId(), new HashSet<>());
        }

        Set<UUID> messageIds = playerMessages.get(value.playerId());
        messageIds.add(key);

        super.put(key, value);
        return value;
    }

    public void remove(UUID key) {
        MessageData data = super.get(key);
        super.remove(key);
        Set<UUID> playerMessages = this.playerMessages.get(data.playerId());
        if (playerMessages == null) return;
        playerMessages.remove(key);
    }

    public void removeMessagesByPlayerUUID(UUID playerId) {
        Set<UUID> messages = playerMessages.get(playerId);
        if (messages == null) return;
        for (UUID message : messages) {
            super.remove(message);
        }
        playerMessages.remove(playerId);
    }

    public Set<UUID> getMessagesByPlayerUUID(UUID playerId) {
        return Set.copyOf(playerMessages.get(playerId));
    }

}
