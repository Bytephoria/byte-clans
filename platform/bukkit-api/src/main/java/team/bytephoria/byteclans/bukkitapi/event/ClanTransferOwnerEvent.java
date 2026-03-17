package team.bytephoria.byteclans.bukkitapi.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;

public final class ClanTransferOwnerEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember executor;
    private final ClanMember oldOwner;
    private final ClanMember newOwner;
    private final Clan clan;

    private boolean cancelled;

    public ClanTransferOwnerEvent(
            final @NotNull ClanMember executor,
            final @NotNull ClanMember oldOwner,
            final @NotNull ClanMember newOwner,
            final @NotNull Clan clan
    ) {
        this.executor = executor;
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
        this.clan = clan;
        this.cancelled = false;
    }

    public ClanMember executor() {
        return this.executor;
    }

    public ClanMember oldOwner() {
        return this.oldOwner;
    }

    public ClanMember newOwner() {
        return this.newOwner;
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