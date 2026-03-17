package team.bytephoria.byteclans.bukkitapi.event.member;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;

public final class ClanMemberJoinEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final BukkitClanPlayer player;
    private final Clan clan;

    private boolean cancelled;

    public ClanMemberJoinEvent(
            final @NotNull BukkitClanPlayer player,
            final @NotNull Clan clan
    ) {
        this.player = player;
        this.clan = clan;
        this.cancelled = false;
    }

    public BukkitClanPlayer player() {
        return this.player;
    }

    public Clan clan() {
        return this.clan;
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