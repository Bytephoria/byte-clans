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

    ResponseContext<ClanRequestAlly, ClanAllyRequestSendResult> send(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    );

    ResponseContext<ClanRequestAlly, ClanAllyRequestAcceptResult> accept(
            final @NotNull ClanMember clanMember
    );

    ResponseContext<ClanRequestAlly, ClanAllyRequestDeclineResult> decline(
            final @NotNull ClanMember clanMember
    );

}
