package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.result.ClanAllyAddResult;
import team.bytephoria.byteclans.api.result.ClanAllyRemoveResult;
import team.bytephoria.byteclans.api.result.ClanEnemyAddResult;
import team.bytephoria.byteclans.api.result.ClanEnemyRemoveResult;
import team.bytephoria.byteclans.api.util.response.Response;

public interface ClanRelationManager {

    @NotNull Response<ClanAllyAddResult> addAllyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    );

    @NotNull Response<ClanAllyRemoveResult> removeAllyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    );

    @NotNull Response<ClanEnemyAddResult> addEnemyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    );

    @NotNull Response<ClanEnemyRemoveResult> removeEnemyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    );

}
