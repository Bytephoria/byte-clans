package team.bytephoria.byteclans.bukkitapi.event.diplomacy;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;

public final class ClanEnemyRemoveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember executorMember;
    private final Clan targetClan;

    private boolean cancelled;

    public ClanEnemyRemoveEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull Clan targetClan
    ) {
        this.executorMember = executorMember;
        this.targetClan = targetClan;
    }

    public ClanMember executorMember() {
        return this.executorMember;
    }

    public Clan targetClan() {
        return this.targetClan;
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
