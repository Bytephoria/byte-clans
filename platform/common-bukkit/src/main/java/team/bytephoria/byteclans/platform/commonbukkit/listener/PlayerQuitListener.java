package team.bytephoria.byteclans.platform.commonbukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.spi.loader.UserLoader;

public final class PlayerQuitListener implements Listener {

    private final UserLoader userLoader;
    public PlayerQuitListener(final @NotNull UserLoader userLoader) {
        this.userLoader = userLoader;
    }

    @EventHandler
    public void onPlayerQuitEvent(final @NotNull PlayerQuitEvent quitEvent) {
        this.userLoader.unload(BukkitClanPlayer.wrap(quitEvent.getPlayer()));
    }

}
