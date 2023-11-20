package me.youhavetrouble.chitchat;

import net.kyori.adventure.chat.SignedMessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class SignatureCache extends LinkedHashMap<UUID, SignedMessage.Signature> {

    private final int maxSize;

    protected SignatureCache(int maxSize) {
        super(maxSize, 1, false);
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxSize;
    }

}
