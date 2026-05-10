package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.result.ClanStatisticUpdateResult;
import team.bytephoria.byteclans.api.statistic.StatisticUpdate;
import team.bytephoria.byteclans.api.util.response.Response;

import java.util.Collection;

public interface ClanMemberStatisticManager {

    @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull ClanMember clanMember,
            final @NotNull Collection<StatisticUpdate> updates
    );

    @NotNull Response<ClanStatisticUpdateResult> update(
            final @NotNull ClanMember clanMember,
            final @NotNull StatisticUpdate update
    );

}
