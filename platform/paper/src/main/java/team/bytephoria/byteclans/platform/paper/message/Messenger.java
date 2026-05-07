package team.bytephoria.byteclans.platform.paper.message;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import team.bytephoria.byteclans.infrastructure.adventure.ComponentSerializerAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Messenger {

    private final ConfigurationNode configurationNode;
    private final ComponentSerializerAdapter serializerAdapter;

    public Messenger(
            final @NotNull ConfigurationNode configurationNode,
            final @NotNull ComponentSerializerAdapter serializerAdapter
    ) {
        this.configurationNode = configurationNode;
        this.serializerAdapter = serializerAdapter;
    }

    private @NonNull List<String> getList(final @NotNull Object @NotNull ... paths) {
        try {
            return this.configurationNode.node(paths).getList(String.class, Collections.emptyList());
        } catch (SerializationException e) {
            return Collections.emptyList();
        }
    }

    public void sendPathMessage(final @NotNull Player player, final @NotNull String path) {
        this.sendMessage(player, (Object[]) path.split("\\."));
    }

    public void sendPathMessage(final @NotNull Player player, final @NotNull String path, final @NotNull Map<String, String> replacements){
        this.sendMessage(player, replacements, (Object[]) path.split("\\."));
    }

    public void sendListMessage(final @NotNull Player player, final @NotNull Object @NotNull ... paths) {
        final List<String> list = this.getList(paths);
        for (final String line : list) {
            final Component component = this.serializerAdapter.deserialize(line);
            player.sendMessage(component);
        }

    }

    public void sendListMessage(final @NotNull Player player, Map<String, String> replacements, final @NotNull Object @NotNull ... paths) {
        final List<String> list = this.getList(paths);

        for (final String line : list) {
            String finalMessage = line;
            for (final Map.Entry<String, String> entry : replacements.entrySet()) {
                finalMessage = finalMessage.replace("{" + entry.getKey() + "}", entry.getValue());
            }

            final Component component = this.serializerAdapter.deserialize(finalMessage);
            player.sendMessage(component);
        }
    }

    public void sendMessage(final @NotNull Player player, final @NotNull Object @NotNull ... paths) {
        final String message = this.configurationNode.node(paths).getString();
        if (message == null) {
            return;
        }

        final Component component = this.serializerAdapter.deserialize(message);
        player.sendMessage(component);
    }

    public void sendMessage(final @NotNull Player player, final Map<String, String> replacements, final @NotNull Object... paths) {
        final String message = this.configurationNode.node(paths).getString();
        if (message == null) {
            return;
        }

        String finalMessage = message;
        for (final Map.Entry<String, String> entry : replacements.entrySet()) {
            finalMessage = finalMessage.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        final Component component = this.serializerAdapter.deserialize(finalMessage);
        player.sendMessage(component);
    }

}
