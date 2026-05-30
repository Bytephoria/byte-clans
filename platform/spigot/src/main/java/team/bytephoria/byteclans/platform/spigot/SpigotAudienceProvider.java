package team.bytephoria.byteclans.platform.spigot;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.commonbukkit.AudienceProvider;

public final class SpigotAudienceProvider implements AudienceProvider {

    private final SpigotPlugin spigotPlugin;
    public SpigotAudienceProvider(final @NotNull SpigotPlugin spigotPlugin) {
        this.spigotPlugin = spigotPlugin;
    }

    @Override
    public @NotNull Audience audience(final @NotNull Player player) {
        return this.spigotPlugin.adventure().player(player);
    }
}
