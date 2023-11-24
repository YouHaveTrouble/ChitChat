package me.youhavetrouble.chitchat.renderer;

import io.papermc.paper.chat.ChatRenderer;
import me.clip.placeholderapi.PlaceholderAPI;
import me.youhavetrouble.chitchat.ChitChat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChitChatRenderer implements ChatRenderer {

    private final ChitChat plugin;
    private final UUID messageId;
    private final Component renderedMessage;

    public ChitChatRenderer(
            ChitChat plugin,
            String format,
            SignedMessage signedMessage,
            Player player,
            Component chatMessage
    ) {
        this.plugin = plugin;
        this.messageId = signedMessage != null ? plugin.cacheSignature(signedMessage.signature()) : null;
        Collection<TagResolver> resolvers = new ArrayList<>();
        resolvers.add(StandardTags.defaults());
        resolvers.add(messageResolver(player, chatMessage));
        resolvers.add(playerNameResolver(player.displayName()));
        resolvers.add(placeholderResolver(player));
        MiniMessage formatter = MiniMessage.builder()
                .tags(TagResolver
                        .builder()
                        .resolvers(resolvers)
                        .build()
                ).build();
        this.renderedMessage = formatter.deserialize(format);
    }

    @Override
    public @NotNull Component render(
            @NotNull Player player,
            @NotNull Component playerDisplayName,
            @NotNull Component chatMessage,
            @NotNull Audience audience
    ) {
        if (audience instanceof Player recipentPlayer && recipentPlayer.hasPermission("chitchat.deletemessage")) {
            if (messageId == null) return renderedMessage;
            Component deleteMessageButton = Component.space().append(
                    Component.text("[x]")
                            .color(NamedTextColor.RED)
                            .clickEvent(ClickEvent.clickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/deletemessage " + messageId
                                    )
                            )
                            .hoverEvent(HoverEvent.hoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Component.text("Click to delete this message.")
                            ))
            );
            return renderedMessage.append(deleteMessageButton);
        }
        return renderedMessage;
    }

    private TagResolver playerNameResolver(Component displayName) {
        return TagResolver.resolver(
                "playername",
                (argumentQueue, context) -> Tag.selfClosingInserting(displayName)
        );
    }

    private TagResolver messageResolver(Player player, Component message) {

        Collection<TagResolver> resolvers = new ArrayList<>();

        if (player.hasPermission("chitchat.color")) {
            resolvers.add(StandardTags.color());
        }
        if (player.hasPermission("chitchat.gradient")) {
            resolvers.add(StandardTags.gradient());
        }
        if (player.hasPermission("chitchat.rainbow")) {
            resolvers.add(StandardTags.rainbow());
        }
        if (player.hasPermission("chitchat.font")) {
            resolvers.add(StandardTags.font());
        }

        if (player.hasPermission("chitchat.decoration")) {
            resolvers.add(StandardTags.decorations());
        } else {
            if (player.hasPermission("chitchat.decoration.bold")) {
                resolvers.add(StandardTags.decorations(TextDecoration.BOLD));
            }
            if (player.hasPermission("chitchat.decoration.italic")) {
                resolvers.add(StandardTags.decorations(TextDecoration.ITALIC));
            }
            if (player.hasPermission("chitchat.decoration.underlined")) {
                resolvers.add(StandardTags.decorations(TextDecoration.UNDERLINED));
            }
            if (player.hasPermission("chitchat.decoration.strikethrough")) {
                resolvers.add(StandardTags.decorations(TextDecoration.STRIKETHROUGH));
            }
            if (player.hasPermission("chitchat.decoration.obfuscated")) {
                resolvers.add(StandardTags.decorations(TextDecoration.OBFUSCATED));
            }
        }

        MiniMessage messageParser = MiniMessage.builder()
                .tags(TagResolver
                        .builder()
                        .resolvers(resolvers)
                        .build()
                )
                .build();

        PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();

        Component parsedMessage = messageParser.deserialize(plainTextComponentSerializer.serialize(message));

        return TagResolver.resolver(
                "message",
                (argumentQueue, context) -> Tag.selfClosingInserting(parsedMessage)
        );
    }

    private @NotNull TagResolver placeholderResolver(final @NotNull Player player) {
        return TagResolver.resolver("placeholder", (argumentQueue, context) -> {
            final String placeholder = argumentQueue.popOr("placeholder tag requires an argument").value();
            switch (placeholder) {
                case "name" -> {
                    return Tag.selfClosingInserting(player.name());
                }
                case "displayname" -> {
                    return Tag.selfClosingInserting(player.displayName());
                }
                default -> {
                    if (!plugin.isPapiHooked()) return Tag.selfClosingInserting(Component.text(placeholder));

                    final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');

                    if (parsedPlaceholder.contains(LegacyComponentSerializer.SECTION_CHAR + "")) {
                        Component componentPlaceholder = LegacyComponentSerializer.legacySection()
                                .deserialize(parsedPlaceholder);
                        return Tag.selfClosingInserting(componentPlaceholder);
                    }

                    return Tag.selfClosingInserting(MiniMessage.miniMessage().deserialize(parsedPlaceholder));
                }
            }

        });
    }
}
