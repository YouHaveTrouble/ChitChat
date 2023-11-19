package me.youhavetrouble.chitchat.renderer;

import io.papermc.paper.chat.ChatRenderer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;


public class ChitChatRenderer implements ChatRenderer {

    private final String format;
    private final boolean papiHooked;
    private final Collection<TagResolver> tagResolvers = new ArrayList<>();

    public ChitChatRenderer(String format, boolean papiHooked) {
        this.papiHooked = papiHooked;
        this.format = format;
        this.tagResolvers.add(StandardTags.defaults());
    }

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component playerDisplayName, @NotNull Component chatMessage, @NotNull Audience audience) {

        Collection<TagResolver> resolvers = new ArrayList<>(tagResolvers);
        resolvers.add(messageResolver(player, chatMessage));
        resolvers.add(playerNameResolver(playerDisplayName));
        resolvers.add(placeholderTag(player));

        MiniMessage formatter = MiniMessage
                .builder()
                .tags(
                        TagResolver
                                .builder()
                                .resolvers(
                                        resolvers
                                )
                                .build()
                )
                .build();

        return formatter.deserialize(format);
    }

    private TagResolver messageResolver(Player player, Component message) {

        Collection<TagResolver> resolvers = new ArrayList<>();

        if (player.hasPermission("chitchat.color")) {
            resolvers.add(StandardTags.color());
        }

        if (player.hasPermission("chitchat.gradient")) {
            resolvers.add(StandardTags.gradient());
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

    private TagResolver playerNameResolver(Component displayName) {
        return TagResolver.resolver(
                "playername",
                (argumentQueue, context) -> Tag.selfClosingInserting(displayName)
        );
    }

    private @NotNull TagResolver placeholderTag(final @NotNull Player player) {
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
                    if (!papiHooked) return Tag.selfClosingInserting(Component.text(placeholder));

                    final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');

                    if (parsedPlaceholder.contains(LegacyComponentSerializer.SECTION_CHAR + "")) {
                        Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);
                        return Tag.selfClosingInserting(componentPlaceholder);
                    }

                    return Tag.selfClosingInserting(MiniMessage.miniMessage().deserialize(parsedPlaceholder));
                }
            }

        });
    }
}
