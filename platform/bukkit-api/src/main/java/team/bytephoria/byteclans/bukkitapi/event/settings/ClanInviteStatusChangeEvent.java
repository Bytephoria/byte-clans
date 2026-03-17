package team.bytephoria.byteclans.bukkitapi.event.settings;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanMember;

public final class ClanInviteStatusChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember clanMember;
    private final Clan clan;

    private final ClanInviteState oldState;
    private final ClanInviteState newState;

    private boolean cancelled;

    public ClanInviteStatusChangeEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull ClanInviteState oldState,
            final @NotNull ClanInviteState newState
    ) {
        this.clanMember = clanMember;
        this.clan = clan;
        this.oldState = oldState;
        this.newState = newState;
        this.cancelled = false;
    }

    public ClanMember clanMember() {
        return this.clanMember;
    }

    public Clan clan() {
        return this.clan;
    }

    public ClanInviteState oldState() {
        return this.oldState;
    }

    public ClanInviteState newState() {
        return this.newState;
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