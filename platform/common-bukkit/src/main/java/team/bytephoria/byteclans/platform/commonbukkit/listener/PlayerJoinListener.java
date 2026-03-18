package team.bytephoria.byteclans.platform.commonbukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.spi.loader.UserLoader;

public final class PlayerJoinListener implements Listener {

    private final UserLoader userLoader;
    public PlayerJoinListener(final @NotNull UserLoader userLoader) {
        this.userLoader = userLoader;
    }

    @EventHandler
    public void onPlayerJoinEvent(final @NotNull PlayerJoinEvent joinEvent) {
        this.userLoader.load(BukkitClanPlayer.wrap(joinEvent.getPlayer()));
    }

}
