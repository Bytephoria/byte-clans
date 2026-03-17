package team.bytephoria.byteclans.bukkitapi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanPlayer;

import java.util.UUID;

public final class BukkitClanPlayer implements ClanPlayer {

    private final Player player;
    BukkitClanPlayer(final @NotNull Player player) {
        this.player = player;
    }

    public static @NotNull BukkitClanPlayer wrap(final @NotNull Player player) {
        return new BukkitClanPlayer(player);
    }

    public @NotNull Player bukkitPlayer() {
        return this.player;
    }

    @Override
    public @NonNull UUID uniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public @NonNull String name() {
        return this.player.getName();
    }

}
