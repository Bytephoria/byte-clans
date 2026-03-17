package team.bytephoria.byteclans.bukkitapi.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanRole;

public final class ClanPromoteEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember executorMember;
    private final ClanMember targetMember;

    private final ClanRole currentRole;
    private final ClanRole nextRole;

    private boolean cancelled;

    public ClanPromoteEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull ClanMember targetMember,
            final @NotNull ClanRole currentRole,
            final @NotNull ClanRole nextRole
    ) {
        this.executorMember = executorMember;
        this.targetMember = targetMember;
        this.currentRole = currentRole;
        this.nextRole = nextRole;
        this.cancelled = false;
    }

    public ClanMember executorMember() {
        return this.executorMember;
    }

    public ClanMember targetMember() {
        return this.targetMember;
    }

    public ClanRole currentRole() {
        return this.currentRole;
    }

    public ClanRole nextRole() {
        return this.nextRole;
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
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
