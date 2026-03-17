package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanMemberManager;
import team.bytephoria.byteclans.api.result.*;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.core.clan.DefaultClanOwnerData;
import team.bytephoria.byteclans.core.factory.ClanMemberFactory;
import team.bytephoria.byteclans.core.registry.DefaultClanRoleRegistry;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.storage.ClanMemberStorage;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionManager;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.entry.ClanMemberEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;
import team.bytephoria.byteclans.spi.storage.field.ClanMemberField;

import java.util.Optional;

public final class DefaultClanMemberManager implements ClanMemberManager {

    private final IdentityCachedMap<ClanMember> memberCache;
    private final ClanGlobalSettings clanGlobalSettings;

    private final ClanMemberStorage clanMemberStorage;
    private final ClanStorage clanStorage;

    private final ClanMemberFactory clanMemberFactory;
    private final ClanEventBus clanEventBus;
    private final DefaultClanRoleRegistry roleRegistry;
    private final TransactionManager transactionManager;

    public DefaultClanMemberManager(
            final @NotNull IdentityCachedMap<ClanMember> memberCache,
            final @NotNull ClanGlobalSettings clanGlobalSettings,
            final @NotNull ClanMemberStorage clanMemberStorage,
            final @NotNull ClanStorage clanStorage,
            final @NotNull ClanMemberFactory clanMemberFactory,
            final @NotNull ClanEventBus clanEventBus,
            final @NotNull DefaultClanRoleRegistry clanRoleRegistry,
            final @NotNull TransactionManager transactionManager

    ) {
        this.memberCache = memberCache;
        this.clanGlobalSettings = clanGlobalSettings;
        this.clanMemberStorage = clanMemberStorage;
        this.clanStorage = clanStorage;
        this.clanMemberFactory = clanMemberFactory;
        this.clanEventBus = clanEventBus;
        this.roleRegistry = clanRoleRegistry;
        this.transactionManager = transactionManager;
    }

    @Override
    public @NonNull ResponseContext<ClanMember, ClanJoinResult> join(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull Clan clan
    ) {

        if (!this.clanEventBus.callMemberJoin(clanPlayer, clan)) {
            return ResponseContext.failure(ClanJoinResult.CANCELLED);
        }

        final ClanMember clanMember = this.clanMemberFactory.create(
                clanPlayer,
                clan,
                this.roleRegistry,
                false
        );

        clan.addMember(clanMember);
        this.memberCache.add(clanMember);
        this.clanMemberStorage.async().create(ClanMemberEntry.from(clanMember));
        return ResponseContext.success(clanMember, ClanJoinResult.SUCCESS);
    }

    @Override
    public Response<ClanLeaveResult> leave(
            final @NotNull ClanMember clanMember
    ) {

        final Clan clan = clanMember.clan();
        if (clan == null) {
            return Response.failure(ClanLeaveResult.NOT_IN_CLAN);
        }

        final Optional<ClanMember> optionalClanMember = clan.ownerMember();
        if (optionalClanMember.isPresent() && optionalClanMember.get() == clanMember) {
            return Response.failure(ClanLeaveResult.OWNER_CANNOT_LEAVE);
        }

        if (!this.clanEventBus.callMemberLeave(clanMember)) {
            return Response.failure(ClanLeaveResult.CANCELLED);
        }

        clan.removeMemberByUniqueId(clanMember.uniqueId());

        this.memberCache.remove(clanMember);
        this.clanMemberStorage.async().deleteByUniqueId(clanMember.uniqueId());
        this.clanEventBus.callMemberLeave(clanMember);
        return Response.success(ClanLeaveResult.SUCCESS);
    }

    @Override
    public Response<ClanKickResult> kick(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember
    ) {

        final Clan clan = executorClanMember.clan();
        if (clan == null) {
            return Response.failure(ClanKickResult.NOT_IN_CLAN);
        }

        final Clan targetClan = targetClanMember.clan();
        if (targetClan == null) {
            return Response.failure(ClanKickResult.TARGET_NOT_IN_CLAN);
        }

        if (clan != targetClan) {
            return Response.failure(ClanKickResult.DISTINCT_CLAN);
        }

        if (!executorClanMember.hasPermission(ClanAction.KICK_MEMBERS)) {
            return Response.failure(ClanKickResult.INSUFFICIENT_ROLE);
        }

        if (targetClanMember.role().priority() > executorClanMember.role().priority()) {
            return Response.failure(ClanKickResult.CANNOT_KICK_HIGHER_ROLE);
        }

        final Optional<ClanMember> optionalClanMember = clan.ownerMember();
        if (optionalClanMember.isPresent() && optionalClanMember.get() == targetClanMember) {
            return Response.failure(ClanKickResult.CANNOT_KICK_ONESELF);
        }

        if (!this.clanEventBus.callMemberKick(executorClanMember, targetClanMember)) {
            return Response.failure(ClanKickResult.CANCELLED);
        }

        clan.removeMemberByUniqueId(targetClanMember.uniqueId());
        this.memberCache.remove(targetClanMember);
        this.clanMemberStorage.async().deleteByUniqueId(targetClanMember.uniqueId());

        return Response.success(ClanKickResult.SUCCESS);
    }

    @Override
    public Response<ClanChangeChatModeResult> changeChatMode(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanChatType chatType
    ) {

        if (chatType == ClanChatType.CLAN && clanMember.clan() == null) {
            return Response.failure(ClanChangeChatModeResult.NOT_IN_CLAN);
        }

        if (clanMember.chatType() == chatType) {
            return Response.failure(ClanChangeChatModeResult.ALREADY_IN_MODE);
        }

        if (clanMember.clan().allMembers().size() == 1) {
            return Response.failure(ClanChangeChatModeResult.INSUFFICIENT_ONLINE_MEMBERS);
        }

        clanMember.chatType(chatType);
        return Response.success(ClanChangeChatModeResult.SUCCESS);
    }

    @Override
    public Response<ClanRoleChangeResult> changeRole(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember,
            final @NotNull ClanRole clanRole
    ) {

        final Clan clan = executorClanMember.clan();
        if (clan == null) {
            return Response.failure(ClanRoleChangeResult.NOT_IN_CLAN);
        }

        final Clan targetClan = targetClanMember.clan();
        if (targetClan == null) {
            return Response.failure(ClanRoleChangeResult.TARGET_NOT_IN_CLAN);
        }

        if (clan != targetClan) {
            return Response.failure(ClanRoleChangeResult.DISTINCT_CLAN);
        }

        final Optional<ClanMember> optionalClanMember = clan.ownerMember();
        if (optionalClanMember.isPresent() && optionalClanMember.get() == targetClanMember) {
            return Response.failure(ClanRoleChangeResult.CANNOT_DEMOTE_OWNER);
        }

        final ClanRole oldRole = targetClanMember.role();
        if (oldRole == clanRole) {
            return Response.failure(ClanRoleChangeResult.ALREADY_SET);
        }

        if (!this.clanEventBus.callMemberChangeRole(executorClanMember, targetClanMember, oldRole, clanRole)) {
            return Response.failure(ClanRoleChangeResult.CANCELLED);
        }

        targetClanMember.role(clanRole);
        this.clanMemberStorage.async().update(ClanMemberEntry.from(targetClanMember), ClanMemberField.ROLE_ID);
        return Response.success(ClanRoleChangeResult.SUCCESS);
    }

    @Override
    public Response<ClanPromoteResult> promote(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanMember targetClanMember
    ) {
        if (clanMember == targetClanMember) {
            return Response.failure(ClanPromoteResult.CANNOT_PROMOTE_ONESELF);
        }

        if (clanMember.clan() != targetClanMember.clan()) {
            return Response.failure(ClanPromoteResult.DISTINCT_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.PROMOTE_MEMBER)) {
            return Response.failure(ClanPromoteResult.INSUFFICIENT_ROLE);
        }

        if (targetClanMember.role().priority() >= clanMember.role().priority()) {
            return Response.failure(ClanPromoteResult.CANNOT_PROMOTE_HIGHER_OR_EQUAL_ROLE);
        }

        final ClanRole nextRole = this.roleRegistry
                .getNextRole(targetClanMember.role())
                .orElse(null);

        if (nextRole == null) {
            return Response.failure(ClanPromoteResult.ALREADY_MAX_ROLE);
        }

        if (nextRole.priority() >= clanMember.role().priority()) {
            return Response.failure(ClanPromoteResult.CANNOT_PROMOTE_TO_HIGHER_ROLE);
        }

        final ClanRole currentRole = targetClanMember.role();
        if (!this.clanEventBus.callPromoteEvent(clanMember, targetClanMember, currentRole, nextRole)) {
            return Response.failure(ClanPromoteResult.CANCELLED);
        }

        targetClanMember.role(nextRole);
        this.clanMemberStorage.async().update(ClanMemberEntry.from(targetClanMember), ClanMemberField.ROLE_ID);
        return Response.success(ClanPromoteResult.SUCCESS);
    }

    @Override
    public Response<ClanDemoteResult> demote(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanMember targetClanMember
    ) {

        if (clanMember == targetClanMember) {
            return Response.failure(ClanDemoteResult.CANNOT_DEMOTE_ONESELF);
        }

        if (clanMember.clan() != targetClanMember.clan()) {
            return Response.failure(ClanDemoteResult.DISTINCT_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.PROMOTE_MEMBER)) {
            return Response.failure(ClanDemoteResult.INSUFFICIENT_ROLE);
        }

        if (targetClanMember.role().priority() >= clanMember.role().priority()) {
            return Response.failure(ClanDemoteResult.CANNOT_DEMOTE_HIGHER_OR_EQUAL_ROLE);
        }

        final ClanRole previousRole = this.roleRegistry.getPreviousRole(targetClanMember.role())
                .orElse(null);

        if (previousRole == null || targetClanMember.role().isDefault()) {
            return Response.failure(ClanDemoteResult.ALREADY_MIN_ROLE);
        }

        final ClanRole currentRole = targetClanMember.role();
        if (!this.clanEventBus.callDemoteEvent(clanMember, targetClanMember, currentRole, previousRole)) {
            return Response.failure(ClanDemoteResult.CANCELLED);
        }

        targetClanMember.role(previousRole);
        this.clanMemberStorage.async().update(ClanMemberEntry.from(targetClanMember), ClanMemberField.ROLE_ID);
        return Response.success(ClanDemoteResult.SUCCESS);
    }

    @Override
    public Response<ClanTransferResult> transferOwner(
            final @NotNull ClanMember executorClanMember,
            final @NotNull ClanMember targetClanMember
    ) {

        final Clan clan = executorClanMember.clan();
        if (clan == null) {
            return Response.failure(ClanTransferResult.NOT_IN_CLAN);
        }

        final Clan targetClan = targetClanMember.clan();
        if (targetClan == null) {
            return Response.failure(ClanTransferResult.TARGET_NOT_IN_CLAN);
        }

        if (clan != targetClan) {
            return Response.failure(ClanTransferResult.DISTINCT_CLAN);
        }

        final Optional<ClanMember> optionalClanMember = clan.ownerMember();
        if (optionalClanMember.isEmpty()) {
            return Response.failure(ClanTransferResult.OWNER_NOT_FOUND);
        }

        final ClanMember clanMember = optionalClanMember.get();
        if (clanMember == targetClanMember) {
            return Response.failure(ClanTransferResult.TARGET_IS_ALREADY_OWNER);
        }

        final ClanMember oldOwner = optionalClanMember.get();
        if (!this.clanEventBus.callTransferOwner(executorClanMember, oldOwner, targetClanMember, clan)) {
            return Response.failure(ClanTransferResult.CANCELLED);
        }

        executorClanMember.role(this.roleRegistry.getDefaultRole());
        targetClanMember.role(this.roleRegistry.getOwnerRole());

        clan.ownerData(DefaultClanOwnerData.from(targetClanMember));
        clan.ownerMember(targetClanMember);

        this.transactionManager.execute(() -> {
            this.clanStorage.update(ClanEntry.from(clan), ClanField.OWNER_UNIQUE_ID, ClanField.OWNER_NAME);
            this.clanMemberStorage.update(ClanMemberEntry.from(executorClanMember), ClanMemberField.ROLE_ID);
            this.clanMemberStorage.update(ClanMemberEntry.from(targetClanMember), ClanMemberField.ROLE_ID);
        });

        return Response.success(ClanTransferResult.SUCCESS);
    }
}
