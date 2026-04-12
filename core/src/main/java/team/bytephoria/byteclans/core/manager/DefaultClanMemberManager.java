package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.Contract;
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
import team.bytephoria.byteclans.core.util.ClanNameUUID;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.storage.ClanMemberStorage;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.entry.ClanMemberEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;
import team.bytephoria.byteclans.spi.storage.field.ClanMemberField;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionManager;
import team.bytephoria.byteclans.spi.storage.view.ClanMemberView;
import team.bytephoria.byteclans.spi.storage.view.ClanView;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public final class DefaultClanMemberManager implements ClanMemberManager {

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> memberCache;
    private final ClanGlobalSettings clanGlobalSettings;

    private final ClanMemberStorage clanMemberStorage;
    private final ClanStorage clanStorage;

    private final ClanMemberFactory clanMemberFactory;
    private final ClanEventBus clanEventBus;
    private final DefaultClanRoleRegistry roleRegistry;
    private final TransactionManager transactionManager;

    public DefaultClanMemberManager(
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> memberCache,
            final @NotNull ClanGlobalSettings clanGlobalSettings,
            final @NotNull ClanMemberStorage clanMemberStorage,
            final @NotNull ClanStorage clanStorage,
            final @NotNull ClanMemberFactory clanMemberFactory,
            final @NotNull ClanEventBus clanEventBus,
            final @NotNull DefaultClanRoleRegistry clanRoleRegistry,
            final @NotNull TransactionManager transactionManager

    ) {
        this.clanCache = clanCache;
        this.memberCache = memberCache;
        this.clanGlobalSettings = clanGlobalSettings;
        this.clanMemberStorage = clanMemberStorage;
        this.clanStorage = clanStorage;
        this.clanMemberFactory = clanMemberFactory;
        this.clanEventBus = clanEventBus;
        this.roleRegistry = clanRoleRegistry;
        this.transactionManager = transactionManager;
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Admin admin() {
        return new Admin() {

            private DefaultClanMemberManager thisInstance() {
                return DefaultClanMemberManager.this;
            }

            @Override
            public Response<ClanKickResult> kick(final @NotNull UUID memberUniqueId) {
                final ClanMember clanMember = this.thisInstance().memberCache.get(memberUniqueId);
                if (clanMember != null) {
                    final Clan clan = clanMember.clan();
                    if (clan.ownerData().uniqueId().equals(memberUniqueId)) {
                        return Response.failure(ClanKickResult.CANNOT_KICK_OWNER);
                    }

                    clan.removeMemberByUniqueId(memberUniqueId);
                    this.thisInstance().memberCache.remove(memberUniqueId);
                    this.thisInstance().clanMemberStorage.deleteByUniqueId(memberUniqueId);
                    return ResponseContext.success(clanMember, ClanKickResult.SUCCESS);
                }

                final ClanMemberView memberView = this.thisInstance().clanMemberStorage
                        .findByUniqueId(memberUniqueId)
                        .orElse(null);

                if (memberView == null) {
                    return Response.failure(ClanKickResult.TARGET_NOT_IN_CLAN);
                }

                final ClanView clanView = this.thisInstance().clanStorage
                        .findByUniqueId(memberView.clanUniqueId())
                        .orElse(null);

                if (clanView != null && clanView.ownerUniqueId().equals(memberUniqueId)) {
                    return Response.failure(ClanKickResult.CANNOT_KICK_OWNER);
                }

                this.thisInstance().clanMemberStorage.deleteByUniqueId(memberUniqueId);
                return Response.success(ClanKickResult.SUCCESS);
            }

            @Override
            public Response<ClanTransferResult> transfer(
                    final @NotNull String clanName,
                    final @NotNull UUID newOwnerUniqueId,
                    final @NotNull String newOwnerName
            ) {
                final UUID clanUniqueId = ClanNameUUID.from(clanName);
                return this.transfer(clanUniqueId, newOwnerUniqueId, newOwnerName);
            }

            @Override
            public Response<ClanTransferResult> transfer(
                    final @NotNull UUID clanUniqueId,
                    final @NotNull UUID newOwnerUniqueId,
                    final @NotNull String newOwnerName
            ) {
                final Clan clan = this.thisInstance().clanCache.get(clanUniqueId);
                if (clan == null) {
                    return Response.failure(ClanTransferResult.NOT_IN_CLAN);
                }

                if (clan.ownerData().uniqueId().equals(newOwnerUniqueId)) {
                    return Response.failure(ClanTransferResult.TARGET_IS_ALREADY_OWNER);
                }

                final ClanMember newOwnerMember = this.thisInstance().memberCache.get(newOwnerUniqueId);
                final boolean newOwnerExistsInDB = newOwnerMember == null &&
                        this.thisInstance().clanMemberStorage.findByUniqueId(newOwnerUniqueId)
                                .map(view -> view.clanUniqueId().equals(clanUniqueId))
                                .orElse(false);

                if (newOwnerMember == null && !newOwnerExistsInDB) {
                    return Response.failure(ClanTransferResult.TARGET_NOT_IN_CLAN);
                }

                final ClanMember currentOwner = this.thisInstance().memberCache.get(clan.ownerData().uniqueId());
                if (currentOwner != null) {
                    currentOwner.role(this.thisInstance().roleRegistry.getDefaultRole());
                }

                if (newOwnerMember != null) {
                    newOwnerMember.role(this.thisInstance().roleRegistry.getOwnerRole());
                    clan.ownerMember(newOwnerMember);
                }

                final DefaultClanOwnerData ownerData = new DefaultClanOwnerData(newOwnerName, newOwnerUniqueId);
                clan.ownerData(ownerData);

                this.thisInstance().transactionManager.execute(() -> {
                    this.thisInstance().clanStorage.update(ClanEntry.from(clan), ClanField.OWNER_UNIQUE_ID, ClanField.OWNER_NAME);
                    if (currentOwner != null) {
                        this.thisInstance().clanMemberStorage.update(ClanMemberEntry.from(currentOwner), ClanMemberField.ROLE_ID);
                    }

                    if (newOwnerMember != null) {
                        this.thisInstance().clanMemberStorage.update(ClanMemberEntry.from(newOwnerMember), ClanMemberField.ROLE_ID);
                    }
                });

                return Response.success(ClanTransferResult.SUCCESS);
            }

            @Override
            public Response<ClanRoleChangeResult> changeRole(
                    final @NotNull UUID memberUniqueId,
                    final @NotNull ClanRole clanRole
            ) {
                if (clanRole.equals(this.thisInstance().roleRegistry.getOwnerRole())) {
                    return Response.failure(ClanRoleChangeResult.CANNOT_ASSIGN_OWNER_ROLE);
                }

                final ClanMember clanMember = this.thisInstance().memberCache.get(memberUniqueId);
                if (clanMember != null) {
                    if (clanMember.role().equals(clanRole)) {
                        return Response.failure(ClanRoleChangeResult.ALREADY_SET);
                    }

                    clanMember.role(clanRole);
                    this.thisInstance().clanMemberStorage.update(
                            ClanMemberEntry.from(clanMember),
                            ClanMemberField.ROLE_ID
                    );

                    return Response.success(ClanRoleChangeResult.SUCCESS);
                }

                final boolean exists = this.thisInstance().clanMemberStorage
                        .findByUniqueId(memberUniqueId)
                        .isPresent();

                if (!exists) {
                    return Response.failure(ClanRoleChangeResult.MEMBER_NOT_FOUND);
                }

                this.thisInstance().clanMemberStorage.update(
                        new ClanMemberEntry(memberUniqueId, null, clanRole.id(), null, null, null),
                        ClanMemberField.ROLE_ID
                );

                return Response.success(ClanRoleChangeResult.SUCCESS);
            }
        };
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

        if (chatType == ClanChatType.CLAN && clanMember.clan().allMembers().size() == 1) {
            return Response.failure(ClanChangeChatModeResult.INSUFFICIENT_ONLINE_MEMBERS);
        } else if (chatType == ClanChatType.ALLY) {
            final ClanRelations clanRelations = clanMember.clan().relations();
            final Collection<ClanRelation> allies = clanRelations.allies();
            if (allies.isEmpty()) {
                return Response.failure(ClanChangeChatModeResult.NOT_HAVE_ALLIES);
            } else {
                final boolean hasAtLeastOne = allies.stream().anyMatch(
                        clanRelation -> this.clanCache.get(clanRelation.clanUniqueId()) != null
                );

                if (!hasAtLeastOne) {
                    return Response.failure(ClanChangeChatModeResult.INSUFFICIENT_ONLINE_ALLIES);
                }
            }
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
