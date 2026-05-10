package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.result.*;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;

import java.util.UUID;

public interface ClanMemberManager {

    @NotNull Response<ClanKickResult> kick(final @NotNull UUID memberUniqueId);

    @NotNull Response<ClanTransferResult> transfer(
            final @NotNull String clanName,
            final @NotNull UUID newOwnerUniqueId,
            final @NotNull String newOwnerName
    );

    @NotNull Response<ClanTransferResult> transfer(
            final @NotNull UUID clanUniqueId,
            final @NotNull UUID newOwnerUniqueId,
            final @NotNull String newOwnerName
    );

    @NotNull Response<ClanRoleChangeResult> changeRole(
            final @NotNull UUID memberUniqueId,
            final @NotNull ClanRole clanRole
    );

    @NotNull ResponseContext<ClanMember, ClanJoinResult> join(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull Clan clan
    );

    @NotNull Response<ClanLeaveResult> leave(
            final @NotNull ClanMember clanMember
    );

    @NotNull Response<ClanKickResult> kick(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember
    );

    @NotNull Response<ClanChangeChatModeResult> changeChatMode(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanChatType chatType
    );

    @NotNull Response<ClanRoleChangeResult> changeRole(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember,
            final @NotNull ClanRole clanRole
    );

    @NotNull Response<ClanPromoteResult> promote(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanMember targetClanMember
    );

    @NotNull Response<ClanDemoteResult> demote(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanMember targetClanMember
    );

    @NotNull Response<ClanTransferResult> transferOwner(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember
    );

}
