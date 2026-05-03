package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanStatistics;
import team.bytephoria.byteclans.api.statistic.StatisticType;
import team.bytephoria.byteclans.api.statistic.StatisticUpdate;
import team.bytephoria.byteclans.api.manager.ClanStatisticManager;
import team.bytephoria.byteclans.api.result.ClanStatisticUpdateResult;
import team.bytephoria.byteclans.api.util.IntValue;
import team.bytephoria.byteclans.api.util.Operation;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class DefaultClanStatisticsManager implements ClanStatisticManager {

    private final ClanEventBus clanEventBus;
    private final ClanStorage clanStorage;

    public DefaultClanStatisticsManager(
            final @NotNull ClanEventBus clanEventBus,
            final @NotNull ClanStorage clanStorage
    ) {
        this.clanEventBus = clanEventBus;
        this.clanStorage = clanStorage;
    }

    @Override
    public @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull Clan clan,
            final @NotNull Collection<StatisticUpdate> updates
    ) {
        final ClanStatistics clanStatistics = clan.statistics();
        final List<ClanField> fieldsToUpdate = new ArrayList<>(updates.size());

        for (final StatisticUpdate statisticUpdate : updates) {
            final int value = statisticUpdate.value();
            final StatisticType statisticType = statisticUpdate.statisticType();
            final Operation operation = statisticUpdate.operation();

            if (!this.clanEventBus.callClanStatisticUpdateEvent(clan, value, operation, statisticType)) {
                continue;
            }

            final IntValue intValue = switch (statisticType) {
                case KILLS -> clanStatistics.kills();
                case DEATHS -> clanStatistics.deaths();
                case KILL_STREAK -> clanStatistics.killsStreak();
            };

            final ClanField field = switch (statisticType) {
                case KILLS -> ClanField.KILLS;
                case DEATHS -> ClanField.DEATHS;
                case KILL_STREAK -> ClanField.KILLS_STREAK;
            };

            intValue.value(value, operation);
            fieldsToUpdate.add(field);
        }

        if (fieldsToUpdate.isEmpty()) {
            return Response.failure(ClanStatisticUpdateResult.CANCELLED);
        }

        this.clanStorage.async().update(ClanEntry.from(clan), fieldsToUpdate.toArray(ClanField[]::new));
        return Response.success(ClanStatisticUpdateResult.SUCCESS);
    }

    @Override
    public @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull Clan clan,
            final @NotNull StatisticUpdate statisticUpdate
    ) {
        return this.update(clan, Collections.singletonList(statisticUpdate));
    }
}