package team.bytephoria.byteclans.bukkitapi.event.member;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;

public final class ClanMemberKickEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember kicker;
    private final ClanMember kicked;
    private final Clan clan;

    private boolean cancelled;

    public ClanMemberKickEvent(
            final @NotNull ClanMember kicker,
            final @NotNull ClanMember kicked,
            final @NotNull Clan clan
    ) {
        this.kicker = kicker;
        this.kicked = kicked;
        this.clan = clan;
        this.cancelled = false;
    }

    public ClanMember kicker() {
        return this.kicker;
    }

    public ClanMember kicked() {
        return this.kicked;
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