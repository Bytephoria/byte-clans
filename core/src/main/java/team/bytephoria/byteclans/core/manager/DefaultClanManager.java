package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanManager;
import team.bytephoria.byteclans.api.result.ClanCreateResult;
import team.bytephoria.byteclans.api.result.ClanDisbandResult;
import team.bytephoria.byteclans.api.result.ClanNameValidationResult;
import team.bytephoria.byteclans.api.result.ClanUpdatePointsResult;
import team.bytephoria.byteclans.api.util.Operation;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.api.validator.ClanNameValidator;
import team.bytephoria.byteclans.core.clan.DefaultClan;
import team.bytephoria.byteclans.core.clan.DefaultClanMember;
import team.bytephoria.byteclans.core.factory.ClanFactory;
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

import java.util.UUID;

public final class DefaultClanManager implements ClanManager {

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> memberCache;

    private final ClanStorage clanStorage;
    private final ClanMemberStorage memberStorage;

    private final ClanMemberFactory clanMemberFactory;
    private final ClanFactory clanFactory;
    private final ClanEventBus clanEventBus;
    private final ClanGlobalSettings globalSettings;
    private final DefaultClanRoleRegistry roleRegistry;

    private final ClanNameValidator clanNameValidator;

    public DefaultClanManager(
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> memberCache,
            final @NotNull ClanStorage clanStorage,
            final @NotNull ClanMemberStorage memberStorage,
            final @NotNull ClanMemberFactory clanMemberFactory,
            final @NotNull ClanFactory clanFactory,
            final @NotNull ClanEventBus clanEventBus,
            final @NotNull ClanGlobalSettings globalSettings,
            final @NotNull DefaultClanRoleRegistry roleRegistry,
            final @NotNull ClanNameValidator clanNameValidator
    ) {
        this.clanCache = clanCache;
        this.memberCache = memberCache;
        this.clanStorage = clanStorage;
        this.clanMemberFactory = clanMemberFactory;
        this.clanFactory = clanFactory;
        this.memberStorage = memberStorage;
        this.clanEventBus = clanEventBus;
        this.globalSettings = globalSettings;
        this.roleRegistry = roleRegistry;
        this.clanNameValidator = clanNameValidator;
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NonNull Admin admin() {
        return new Admin() {

            private DefaultClanManager thisInstance() {
                return DefaultClanManager.this;
            }

            @Override
            public ResponseContext<Clan, ClanDisbandResult> disbandClanByName(final @NotNull String clanName) {
                final UUID clanUniqueId = ClanNameUUID.from(clanName);
                return this.disband(clanUniqueId);
            }

            @Override
            public ResponseContext<Clan, ClanDisbandResult> disbandClanByUniqueId(final @NotNull UUID uniqueId) {
                return this.disband(uniqueId);
            }

            private ResponseContext<Clan, ClanDisbandResult> disband(final @NotNull UUID clanUniqueId) {
                final Clan clan = this.thisInstance().clanCache.get(clanUniqueId);
                if (clan == null) {
                    final boolean exists = this.thisInstance().clanStorage.existsByUniqueId(clanUniqueId);
                    if (!exists) {
                        return ResponseContext.failure(ClanDisbandResult.NOT_EXISTS);
                    }

                    this.thisInstance().clanStorage.deleteByUniqueId(clanUniqueId);
                    return ResponseContext.failure(ClanDisbandResult.SUCCESS);
                }

                clan.allMembers().forEach(member ->
                        this.thisInstance().memberCache.remove(member.uniqueId())
                );

                this.thisInstance().clanCache.remove(clanUniqueId);
                this.thisInstance().clanStorage.deleteByUniqueId(clanUniqueId);
                return ResponseContext.success(clan, ClanDisbandResult.SUCCESS);
            }
        };
    }

    @Override
    public @NonNull ResponseContext<Clan, ClanCreateResult> createClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull String clanName
    ) {

        final ClanMember clanMemberCached = this.memberCache.get(clanPlayer.uniqueId());
        if (clanMemberCached != null) {
            return ResponseContext.failure(ClanCreateResult.ALREADY_IN_CLAN);
        }

        final ClanNameValidationResult nameResult = this.clanNameValidator.validate(clanName);
        if (nameResult != ClanNameValidationResult.VALID) {
            return ResponseContext.failure(ClanCreateResult.valueOf(nameResult.name()));
        }

        if (!this.clanEventBus.callPreCreateClan(clanPlayer, clanName)) {
            return ResponseContext.failure(ClanCreateResult.CANCELLED);
        }

        final DefaultClanMember ownerMember = this.clanMemberFactory.createWithoutClan(
                clanPlayer,
                this.roleRegistry,
                true
        );

        final DefaultClan clan = this.clanFactory.create(
                ownerMember,
                clanName,
                this.globalSettings
        );

        ownerMember.clan(clan);

        this.clanCache.add(clan);
        this.memberCache.add(ownerMember);
        this.clanEventBus.callPostCreateClan(clanPlayer, ownerMember, clan);

        this.clanStorage.async().create(ClanEntry.from(clan))
                .thenCompose(ignored -> this.memberStorage.async().create(ClanMemberEntry.from(ownerMember)));

        return ResponseContext.success(clan, ClanCreateResult.SUCCESS);
    }

    @Override
    public ResponseContext<Clan, ClanDisbandResult> disbandClan(final @NotNull ClanPlayer clanPlayer) {
        final ClanMember clanMember = this.memberCache.get(clanPlayer);
        if (clanMember == null) {
            return ResponseContext.failure(ClanDisbandResult.NOT_IN_CLAN);
        }

        return this.disbandClan(clanMember, clanPlayer);
    }

    @Override
    public ResponseContext<Clan, ClanDisbandResult> disbandClan(final @NotNull ClanMember clanMember) {
        final ClanPlayer clanPlayer = clanMember.player().orElse(null);
        return this.disbandClan(clanMember, clanPlayer);
    }

    @Override
    public @NotNull Response<ClanUpdatePointsResult> updatePoints(
            final @NotNull Clan clan,
            final int value,
            final @NotNull Operation operation
    ) {

        final int oldValue = clan.points().value();
        int newValue = operation.resolve(oldValue, value);

        final int min = this.globalSettings.minimumPoints();
        final int max = this.globalSettings.maximumPoints();

        if (newValue < min) {
            newValue = min;
        }

        if (max > -1 && newValue > max) {
            newValue = max;
        }

        if (newValue == oldValue) {
            return ResponseContext.failure(ClanUpdatePointsResult.SAME_VALUE);
        }

        if (!this.clanEventBus.callClanPointsChangeEvent(clan, value, oldValue, newValue, operation)) {
            return Response.failure(ClanUpdatePointsResult.CANCELLED);
        }

        clan.points().value(newValue);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.POINTS);
        return ResponseContext.success(clan, ClanUpdatePointsResult.SUCCESS);
    }

    private ResponseContext<Clan, ClanDisbandResult> disbandClan(
            final @NotNull ClanMember clanMember,
            final @Nullable ClanPlayer clanPlayer
    ) {
        if (clanMember.clan() == null) {
            return ResponseContext.failure(ClanDisbandResult.NOT_IN_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.DISBAND_CLAN)) {
            return ResponseContext.failure(ClanDisbandResult.INSUFFICIENT_ROLE);
        }

        final Clan clan = clanMember.clan();
        if (clanPlayer != null && !this.clanEventBus.callDisbandClan(clanPlayer, clanMember, clan)) {
            return ResponseContext.failure(ClanDisbandResult.CANCELLED);
        }

        clan.allMembers().forEach(this.memberCache::remove);
        this.clanCache.remove(clan);
        this.clanStorage.async().deleteByUniqueId(clan.uniqueId());

        return ResponseContext.success(clan, ClanDisbandResult.SUCCESS);
    }

}
