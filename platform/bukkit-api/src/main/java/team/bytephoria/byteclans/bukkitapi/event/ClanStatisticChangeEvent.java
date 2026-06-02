package team.bytephoria.byteclans.bukkitapi.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.statistic.StatisticUpdate;

public final class ClanStatisticChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST =  new HandlerList();

    private final Clan clan;
    private final StatisticUpdate statisticUpdate;
    private boolean cancelled;

    public ClanStatisticChangeEvent(
            final @NotNull Clan clan,
            final @NotNull StatisticUpdate statisticUpdate
    ) {
        this.clan = clan;
        this.statisticUpdate = statisticUpdate;
        this.cancelled = false;
    }

    public Clan clan() {
        return this.clan;
    }

    public StatisticUpdate statisticUpdate() {
        return this.statisticUpdate;
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
