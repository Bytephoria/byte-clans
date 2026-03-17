package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.result.*;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;

public interface ClanMemberManager {

    ResponseContext<ClanMember, ClanJoinResult> join(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull Clan clan
    );

    Response<ClanLeaveResult> leave(
            final @NotNull ClanMember clanMember
    );

    Response<ClanKickResult> kick(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember
    );

    Response<ClanChangeChatModeResult> changeChatMode(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanChatType chatType
    );

    Response<ClanRoleChangeResult> changeRole(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember,
            final @NotNull ClanRole clanRole
    );

    Response<ClanPromoteResult> promote(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanMember targetClanMember
    );

    Response<ClanDemoteResult> demote(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanMember targetClanMember
    );

    Response<ClanTransferResult> transferOwner(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember
    );

}
