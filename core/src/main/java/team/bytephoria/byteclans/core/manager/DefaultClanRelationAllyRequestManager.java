package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanRelationAllyRequestManager;
import team.bytephoria.byteclans.api.manager.ClanRelationManager;
import team.bytephoria.byteclans.api.result.ClanAllyRequestAcceptResult;
import team.bytephoria.byteclans.api.result.ClanAllyRequestDeclineResult;
import team.bytephoria.byteclans.api.result.ClanAllyRequestSendResult;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.core.cache.ClanRelationAllyRequestCache;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;

public final class DefaultClanRelationAllyRequestManager implements ClanRelationAllyRequestManager {

    private final ClanRelationAllyRequestCache requestCache;
    private final ClanRelationManager relationManager;

    private final IdentityCachedMap<Clan> clanCache;
    private final ClanEventBus clanEventBus;

    public DefaultClanRelationAllyRequestManager(
            final @NotNull ClanRelationAllyRequestCache requestCache,
            final @NotNull ClanRelationManager relationManager,
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull ClanEventBus clanEventBus
    ) {
        this.requestCache = requestCache;
        this.relationManager = relationManager;
        this.clanCache = clanCache;
        this.clanEventBus = clanEventBus;
    }

    @Override
    public ResponseContext<ClanRequestAlly, ClanAllyRequestSendResult> send(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        final Clan clan = clanMember.clan();
        if (clan == null) {
            return ResponseContext.failure(ClanAllyRequestSendResult.NOT_IN_CLAN);
        } else if (clan == targetClan) {
            return ResponseContext.failure(ClanAllyRequestSendResult.SAME_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.MANAGE_DIPLOMACY)) {
            return ResponseContext.failure(ClanAllyRequestSendResult.NO_PERMISSION);
        }

        final ClanRelationType clanRelationType = clan.relations().getRelationType(targetClan.uniqueId());
        if (clanRelationType == ClanRelationType.ALLIANCE) {
            return ResponseContext.failure(ClanAllyRequestSendResult.ALREADY_ALLIES);
        } else if (clanRelationType == ClanRelationType.TENSION) {
            return ResponseContext.failure(ClanAllyRequestSendResult.IN_TENSION);
        } else if (clanRelationType == ClanRelationType.ENEMY) {
            return ResponseContext.failure(ClanAllyRequestSendResult.IS_ENEMY);
        }

        if (targetClan.relations().isEnemy(clan.uniqueId())) {
            return ResponseContext.failure(ClanAllyRequestSendResult.TARGET_HAS_CLAN_AS_ENEMY);
        }

        if (targetClan.relations().getRelationType(clan.uniqueId()) == ClanRelationType.TENSION) {
            return ResponseContext.failure(ClanAllyRequestSendResult.IN_TENSION);
        }

        final ClanRequestAlly cachedClanRequestAlly = this.requestCache.find(targetClan.uniqueId());
        if (cachedClanRequestAlly != null && cachedClanRequestAlly.clanSenderUniqueId().equals(clan.uniqueId())) {
            return ResponseContext.failure(ClanAllyRequestSendResult.ALREADY_REQUESTED);
        }

        if (!this.clanEventBus.callClanAllyRequestSendEvent(clanMember, targetClan)) {
            return ResponseContext.failure(ClanAllyRequestSendResult.CANCELLED);
        }

        final ClanRequestAlly clanRequestAlly = new ClanRequestAlly(clanMember.uniqueId(), clan.uniqueId(), targetClan.uniqueId());
        this.requestCache.add(clanRequestAlly);
        return ResponseContext.success(clanRequestAlly, ClanAllyRequestSendResult.SUCCESS);
    }

    @Override
    public ResponseContext<ClanRequestAlly, ClanAllyRequestAcceptResult> accept(final @NotNull ClanMember clanMember) {
        final Clan clan = clanMember.clan();
        if (clan == null) {
            return ResponseContext.failure(ClanAllyRequestAcceptResult.NOT_IN_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.MANAGE_DIPLOMACY)) {
            return ResponseContext.failure(ClanAllyRequestAcceptResult.NO_PERMISSION);
        }

        final ClanRequestAlly clanRequestAlly = this.requestCache.remove(clan.uniqueId());
        if (clanRequestAlly == null) {
            return ResponseContext.failure(ClanAllyRequestAcceptResult.NOT_REQUESTED);
        }

        final Clan targetClan = this.clanCache.get(clanRequestAlly.clanSenderUniqueId());
        if (targetClan == null) {
            return ResponseContext.failure(ClanAllyRequestAcceptResult.TARGET_CLAN_OFFLINE);
        }

        this.clanEventBus.callClanAllyRequestAcceptEvent(clanMember, targetClan);
        this.relationManager.addAllyClan(clanMember, targetClan);
        return ResponseContext.success(clanRequestAlly, ClanAllyRequestAcceptResult.SUCCESS);
    }

    @Override
    public ResponseContext<ClanRequestAlly, ClanAllyRequestDeclineResult> decline(final @NotNull ClanMember clanMember) {
        final Clan clan = clanMember.clan();
        if (clan == null) {
            return ResponseContext.failure(ClanAllyRequestDeclineResult.NOT_IN_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.MANAGE_DIPLOMACY)) {
            return ResponseContext.failure(ClanAllyRequestDeclineResult.NO_PERMISSION);
        }

        final ClanRequestAlly clanRequestAlly = this.requestCache.remove(clan.uniqueId());
        if (clanRequestAlly == null) {
            return ResponseContext.failure(ClanAllyRequestDeclineResult.NOT_REQUESTED);
        }

        this.clanEventBus.callClanAllyRequestDeclineEvent(clanMember, clanRequestAlly.clanReceiverUniqueId());
        return ResponseContext.success(clanRequestAlly, ClanAllyRequestDeclineResult.SUCCESS);
    }

}
