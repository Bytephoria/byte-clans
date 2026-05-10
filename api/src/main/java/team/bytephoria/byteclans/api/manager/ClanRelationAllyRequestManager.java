package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanRequestAlly;
import team.bytephoria.byteclans.api.result.ClanAllyRequestAcceptResult;
import team.bytephoria.byteclans.api.result.ClanAllyRequestDeclineResult;
import team.bytephoria.byteclans.api.result.ClanAllyRequestSendResult;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;

public interface ClanRelationAllyRequestManager {

    @NotNull ResponseContext<ClanRequestAlly, ClanAllyRequestSendResult> send(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    );

    @NotNull ResponseContext<ClanRequestAlly, ClanAllyRequestAcceptResult> accept(
            final @NotNull ClanMember clanMember
    );

    @NotNull ResponseContext<ClanRequestAlly, ClanAllyRequestDeclineResult> decline(
            final @NotNull ClanMember clanMember
    );

}
