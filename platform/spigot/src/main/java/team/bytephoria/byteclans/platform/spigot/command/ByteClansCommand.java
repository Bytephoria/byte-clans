package team.bytephoria.byteclans.platform.spigot.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.extension.ClanExtension;
import team.bytephoria.byteclans.api.extension.ExtensionMeta;
import team.bytephoria.byteclans.platform.commonbukkit.extension.ClanExtensionManager;
import team.bytephoria.byteclans.platform.spigot.SpigotPlugin;

import java.util.LinkedList;
import java.util.List;

public final class ByteClansCommand {

    private final ClanExtensionManager clanExtensionManager;
    private final SpigotPlugin spigotPlugin;

    public ByteClansCommand(
            final @NotNull ClanExtensionManager clanExtensionManager,
            final @NotNull SpigotPlugin spigotPlugin
    ) {
        this.clanExtensionManager = clanExtensionManager;
        this.spigotPlugin = spigotPlugin;
    }

    @Command("byte-clans extensions list")
    public void extensionsList(final @NotNull Player player) {
        final List<ExtensionMeta> extensionMetas = this.clanExtensionManager.registered()
                .stream()
                .map(ClanExtension::extensionMeta)
                .toList();

        if (extensionMetas.isEmpty()) {
            this.spigotPlugin.adventure().player(player).sendMessage(Component.text("There are not extensions loaded!", NamedTextColor.RED));
            return;
        }

        final List<Component> components = new LinkedList<>();
        final Component header = Component.text("------------------------------------------------", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH);
        components.add(header);
        components.add(Component.newline());

        for (final ExtensionMeta extensionMeta : extensionMetas) {
            final Component component = Component.empty()
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text(extensionMeta.name(), NamedTextColor.AQUA))
                    .append(Component.space())
                    .append(Component.text("(%s)".formatted(extensionMeta.version()), NamedTextColor.GRAY))
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text(extensionMeta.description(), NamedTextColor.GRAY));

            components.add(component);
        }

        components.add(Component.newline());
        final Component footer = Component.text("------------------------------------------------", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH);
        components.add(footer);

        Component component = Component.empty();
        for (final Component component1 : components) {
            component = component.append(component1);
        }

        spigotPlugin.adventure().player(player).sendMessage(component);
    }

}
