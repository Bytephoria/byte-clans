package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanInviteManager;
import team.bytephoria.byteclans.api.manager.ClanMemberManager;
import team.bytephoria.byteclans.api.result.ClanInviteAcceptResult;
import team.bytephoria.byteclans.api.result.ClanInviteDeclineResult;
import team.bytephoria.byteclans.api.result.ClanInviteSendResult;
import team.bytephoria.byteclans.api.result.ClanJoinResult;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.core.cache.ClanInvitationCache;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;

public final class DefaultClanInviteManager implements ClanInviteManager {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final IdentityCachedMap<Clan> clanCache;

    private final ClanMemberManager clanMemberManager;
    private final ClanInvitationCache clanInvitationCache;
    private final ClanEventBus clanEventBus;

    public DefaultClanInviteManager(
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull ClanMemberManager clanMemberManager,
            final @NotNull ClanInvitationCache clanInvitationCache,
            final @NotNull ClanEventBus clanEventBus
    ) {
        this.clanMemberCache = clanMemberCache;
        this.clanCache = clanCache;
        this.clanMemberManager =  clanMemberManager;
        this.clanInvitationCache = clanInvitationCache;
        this.clanEventBus = clanEventBus;
    }

    @Override
    public ResponseContext<ClanInvitation, ClanInviteAcceptResult> accept(final @NotNull ClanPlayer clanPlayer) {
        final ClanInvitation clanInvitation = this.clanInvitationCache.remove(clanPlayer.uniqueId());
        if (clanInvitation == null) {
            return ResponseContext.failure(ClanInviteAcceptResult.NO_PENDING_INVITE);
        }

        final ClanMember existingMember = this.clanMemberCache.get(clanPlayer);
        if (existingMember != null) {
            return ResponseContext.failure(ClanInviteAcceptResult.ALREADY_IN_CLAN);
        }

        final Clan playerClan = this.clanCache.get(clanPlayer);
        if (playerClan != null && playerClan.isMembersFull()) {
            return ResponseContext.failure(ClanInviteAcceptResult.CLAN_FULL);
        }

        final Clan targetClan = this.clanCache.get(clanInvitation.clanUniqueId());
        if (targetClan == null) {
            return ResponseContext.failure(ClanInviteAcceptResult.NOT_EXISTS);
        }

        if (!this.clanEventBus.callPreInviteAccept(clanPlayer, clanInvitation, targetClan)) {
            return ResponseContext.failure(ClanInviteAcceptResult.CANCELLED);
        }

        final ResponseContext<ClanMember, ClanJoinResult> joinResult = this.clanMemberManager.join(clanPlayer, targetClan);
        if (!joinResult.success()) {
            return ResponseContext.failure(ClanInviteAcceptResult.CANCELLED);
        }

        final ClanMember joinedMember = joinResult.value();
        this.clanEventBus.callPostInviteAccept(clanPlayer, clanInvitation, joinedMember, targetClan);
        return ResponseContext.success(clanInvitation, ClanInviteAcceptResult.SUCCESS);
    }

    @Override
    public @NotNull ResponseContext<ClanInvitation, ClanInviteSendResult> send(
            final @NotNull ClanMember senderMember,
            final @NotNull ClanPlayer targetPlayer
    ) {

        final Clan clan = senderMember.clan();
        if (clan == null) {
            return ResponseContext.failure(ClanInviteSendResult.NOT_IN_CLAN);
        }

        if (!senderMember.hasPermission(ClanAction.INVITE_MEMBERS)) {
            return ResponseContext.failure(ClanInviteSendResult.INSUFFICIENT_ROLE);
        }

        if (clan.settings().inviteState() == ClanInviteState.CLOSED) {
            return ResponseContext.failure(ClanInviteSendResult.INVITES_CLOSED);
        }

        if (senderMember.uniqueId().equals(targetPlayer.uniqueId())) {
            return ResponseContext.failure(ClanInviteSendResult.CANNOT_INVITED_ONESELF);
        }

        if (senderMember.clan().isMembersFull()) {
            return ResponseContext.failure(ClanInviteSendResult.CLAN_FULL);
        }

        final ClanInvitation pendingInvitation = this.clanInvitationCache.getInvitation(targetPlayer.uniqueId());
        if (pendingInvitation != null) {
            return ResponseContext.failure(ClanInviteSendResult.ALREADY_INVITED);
        }

        final ClanMember targetMember = this.clanMemberCache.get(targetPlayer);
        if (targetMember != null) {
            return ResponseContext.failure(ClanInviteSendResult.ALREADY_IN_CLAN);
        }

        if (!this.clanEventBus.callInviteSend(senderMember, clan, targetPlayer)) {
            return ResponseContext.failure(ClanInviteSendResult.CANCELLED);
        }

        final ClanInvitation clanInvitation = new ClanInvitation(
                senderMember.uniqueId(),
                targetPlayer.uniqueId(),
                senderMember.clan().uniqueId()
        );

        this.clanInvitationCache.add(clanInvitation);

        return ResponseContext.success(clanInvitation, ClanInviteSendResult.SUCCESS);
    }

    @Override
    public ResponseContext<ClanInvitation, ClanInviteDeclineResult> decline(final @NotNull ClanPlayer clanPlayer) {
        final ClanInvitation clanInvitation = this.clanInvitationCache.remove(clanPlayer.uniqueId());
        if (clanInvitation == null) {
            return ResponseContext.failure(ClanInviteDeclineResult.NO_PENDING_INVITE);
        }

        this.clanEventBus.callInviteDecline(clanPlayer, clanInvitation);
        return ResponseContext.success(clanInvitation, ClanInviteDeclineResult.SUCCESS);
    }
}
