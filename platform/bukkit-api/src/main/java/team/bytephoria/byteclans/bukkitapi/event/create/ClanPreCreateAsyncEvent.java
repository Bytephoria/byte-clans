package team.bytephoria.byteclans.bukkitapi.event.create;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;

public final class ClanPreCreateAsyncEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final BukkitClanPlayer bukkitClanPlayer;
    private final String clanName;

    private boolean cancelled;

    public ClanPreCreateAsyncEvent(
            final @NotNull BukkitClanPlayer bukkitClanPlayer,
            final @NotNull String clanName
    ) {
        super(true);
        this.bukkitClanPlayer = bukkitClanPlayer;
        this.clanName = clanName;
        this.cancelled = false;
    }

    public BukkitClanPlayer clanPlayer() {
        return this.bukkitClanPlayer;
    }

    public String clanName() {
        return this.clanName;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
