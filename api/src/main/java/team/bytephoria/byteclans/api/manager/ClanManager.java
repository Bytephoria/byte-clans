package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.result.ClanCreateResult;
import team.bytephoria.byteclans.api.result.ClanDisbandResult;
import team.bytephoria.byteclans.api.result.ClanUpdatePointsResult;
import team.bytephoria.byteclans.api.util.Operation;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;

import java.util.UUID;

public interface ClanManager {

    @NotNull ResponseContext<Clan, ClanDisbandResult> disbandClan(
            final @NotNull String clanName
    );

    @NotNull ResponseContext<Clan, ClanDisbandResult> disbandClan(
            final @NotNull UUID uniqueId
    );

    @NotNull ResponseContext<Clan, ClanCreateResult> createClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull String clanName
    );

    @NotNull ResponseContext<Clan, ClanDisbandResult> disbandClan(
            final @NotNull ClanPlayer clanPlayer
    );

    @NotNull ResponseContext<Clan, ClanDisbandResult> disbandClan(
            final @NotNull ClanMember clanMember
    );

    @NotNull Response<ClanUpdatePointsResult> updatePoints(
            final @NotNull Clan clan,
            final int value,
            final @NotNull Operation operation
    );

}
