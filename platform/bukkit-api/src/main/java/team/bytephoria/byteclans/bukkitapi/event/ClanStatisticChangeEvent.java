package team.bytephoria.byteclans.bukkitapi.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.statistic.StatisticUpdate;

public final class ClanStatisticChangeEvent extends Event {

    private static final HandlerList HANDLER_LIST =  new HandlerList();

    private final Clan clan;
    private final StatisticUpdate statisticUpdate;

    public ClanStatisticChangeEvent(
            final @NotNull Clan clan,
            final @NotNull StatisticUpdate statisticUpdate
    ) {
        this.clan = clan;
        this.statisticUpdate = statisticUpdate;
    }

    public Clan clan() {
        return this.clan;
    }

    public StatisticUpdate statisticUpdate() {
        return this.statisticUpdate;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
