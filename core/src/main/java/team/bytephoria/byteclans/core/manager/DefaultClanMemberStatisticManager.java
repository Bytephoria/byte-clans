package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanMemberStatistics;
import team.bytephoria.byteclans.api.manager.ClanMemberStatisticManager;
import team.bytephoria.byteclans.api.result.ClanStatisticUpdateResult;
import team.bytephoria.byteclans.api.statistic.StatisticType;
import team.bytephoria.byteclans.api.statistic.StatisticUpdate;
import team.bytephoria.byteclans.api.util.IntValue;
import team.bytephoria.byteclans.api.util.Operation;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.storage.ClanMemberStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanMemberEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanMemberField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class DefaultClanMemberStatisticManager implements ClanMemberStatisticManager {

    private final ClanEventBus clanEventBus;
    private final ClanMemberStorage clanMemberStorage;

    public DefaultClanMemberStatisticManager(
            final @NotNull ClanEventBus clanEventBus,
            final @NotNull ClanMemberStorage clanMemberStorage
    ) {
        this.clanEventBus = clanEventBus;
        this.clanMemberStorage = clanMemberStorage;
    }

    @Override
    public @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull ClanMember clanMember,
            final @NotNull Collection<StatisticUpdate> updates
    ) {
        final ClanMemberStatistics clanStatistics = clanMember.statistics();
        final List<ClanMemberField> fieldsToUpdate = new ArrayList<>(updates.size());

        for (final StatisticUpdate statisticUpdate : updates) {
            final int value = statisticUpdate.value();
            final StatisticType statisticType = statisticUpdate.statisticType();
            final Operation operation = statisticUpdate.operation();

            final IntValue intValue = switch (statisticType) {
                case KILLS -> clanStatistics.kills();
                case DEATHS -> clanStatistics.deaths();
                case POINTS, KILL_STREAK -> null;
            };

            final ClanMemberField field = switch (statisticType) {
                case KILLS -> ClanMemberField.STATISTIC_KILLS;
                case DEATHS -> ClanMemberField.STATISTIC_DEATHS;
                default -> null;
            };

            if (intValue == null) {
                continue;
            }

            intValue.value(value, operation);
            fieldsToUpdate.add(field);
        }

        if (fieldsToUpdate.isEmpty()) {
            return Response.failure(ClanStatisticUpdateResult.CANCELLED);
        }

        this.clanMemberStorage.async().update(ClanMemberEntry.from(clanMember), fieldsToUpdate.toArray(ClanMemberField[]::new));
        return Response.success(ClanStatisticUpdateResult.SUCCESS);
    }

    @Override
    public @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull ClanMember clanMember,
            final @NotNull StatisticUpdate statisticUpdate
    ) {
        return this.update(clanMember, Collections.singletonList(statisticUpdate));
    }
}
