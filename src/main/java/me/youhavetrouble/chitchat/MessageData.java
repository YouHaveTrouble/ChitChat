package me.youhavetrouble.chitchat;

import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record MessageData(
        @NotNull SignedMessage signedMessage,
        @NotNull UUID signatureId,
        @NotNull UUID playerId
) {

}
