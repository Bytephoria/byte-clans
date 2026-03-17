package team.bytephoria.byteclans.bukkitapi.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;

public final class ClanDisbandEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final BukkitClanPlayer clanPlayer;
    private final ClanMember clanMember;
    private final Clan clan;

    private boolean cancelled;

    public ClanDisbandEvent(
            final @NotNull BukkitClanPlayer clanPlayer,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    ) {
        this.clanPlayer = clanPlayer;
        this.clanMember = clanMember;
        this.clan = clan;
        this.cancelled = false;
    }

    public BukkitClanPlayer clanPlayer() {
        return this.clanPlayer;
    }

    public ClanMember clanMember() {
        return this.clanMember;
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
