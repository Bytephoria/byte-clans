package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.statistic.StatisticUpdate;
import team.bytephoria.byteclans.api.result.ClanStatisticUpdateResult;
import team.bytephoria.byteclans.api.util.response.Response;

import java.util.Collection;

public interface ClanStatisticManager {

    @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull Clan clan,
            final @NotNull Collection<StatisticUpdate> updates
    );

    @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull Clan clan,
            final @NotNull StatisticUpdate update
    );

}
