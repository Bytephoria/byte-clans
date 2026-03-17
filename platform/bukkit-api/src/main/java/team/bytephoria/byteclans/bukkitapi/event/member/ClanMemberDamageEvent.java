package team.bytephoria.byteclans.bukkitapi.event.member;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanMember;

public final class ClanMemberDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember damager;
    private final ClanMember damaged;

    private boolean cancelled;

    public ClanMemberDamageEvent(
            final @NotNull ClanMember damager,
            final @NotNull ClanMember damaged
    ) {
        this.damager = damager;
        this.damaged = damaged;
        this.cancelled = false;
    }

    public ClanMember damaged() {
        return this.damaged;
    }

    public ClanMember damager() {
        return this.damager;
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
