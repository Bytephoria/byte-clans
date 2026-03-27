package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanRelationManager;
import team.bytephoria.byteclans.api.result.ClanAllyAddResult;
import team.bytephoria.byteclans.api.result.ClanAllyRemoveResult;
import team.bytephoria.byteclans.api.result.ClanEnemyAddResult;
import team.bytephoria.byteclans.api.result.ClanEnemyRemoveResult;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.core.clan.DefaultClanRelation;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.storage.ClanAllyStorage;
import team.bytephoria.byteclans.spi.storage.ClanEnemyStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanAllyEntry;
import team.bytephoria.byteclans.spi.storage.entry.ClanEnemyEntry;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionManager;

import java.util.List;
import java.util.Objects;

public final class DefaultClanRelationManager implements ClanRelationManager {

    private final IdentityCachedMap<Clan> clanCache;
    private final TransactionManager transactionManager;
    private final ClanEventBus clanEventBus;
    private final ClanAllyStorage clanAllyStorage;
    private final ClanEnemyStorage clanEnemyStorage;

    public DefaultClanRelationManager(
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull TransactionManager transactionManager,
            final @NotNull ClanEventBus clanEventBus,
            final @NotNull ClanAllyStorage clanAllyStorage,
            final @NotNull ClanEnemyStorage clanEnemyStorage
    ) {
        this.clanCache = clanCache;
        this.transactionManager = transactionManager;
        this.clanEventBus = clanEventBus;
        this.clanAllyStorage = clanAllyStorage;
        this.clanEnemyStorage = clanEnemyStorage;
    }

    @Override
    public Response<ClanAllyAddResult> addAllyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        final Clan clan = clanMember.clan();
        if (clan == null) {
            return Response.failure(ClanAllyAddResult.NOT_IN_CLAN);
        }

        if (clan == targetClan) {
            return Response.failure(ClanAllyAddResult.SAME_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.MANAGE_DIPLOMACY)) {
            return Response.failure(ClanAllyAddResult.NO_PERMISSION);
        }

        final ClanRelationType clanRelationType = clan.relations().getRelationType(targetClan.uniqueId());
        if (clanRelationType == ClanRelationType.ALLIANCE) {
            return Response.failure(ClanAllyAddResult.ALREADY_ALLIES);
        } else if (clanRelationType == ClanRelationType.TENSION) {
            return Response.failure(ClanAllyAddResult.IN_TENSION);
        } else if (clanRelationType == ClanRelationType.ENEMY) {
            return Response.failure(ClanAllyAddResult.IS_ENEMY);
        }

        final ClanRelationType targetRelationType = targetClan.relations().getRelationType(clan.uniqueId());
        if (targetRelationType == ClanRelationType.TENSION) {
            return Response.failure(ClanAllyAddResult.IN_TENSION);
        } else if (targetRelationType == ClanRelationType.ENEMY) {
            return Response.failure(ClanAllyAddResult.IS_ENEMY);
        }

        if (!this.clanEventBus.callClanAllyAddEvent(clanMember, targetClan)) {
            return Response.failure(ClanAllyAddResult.CANCELLED);
        }

        clan.relations().add(
                new DefaultClanRelation(
                        targetClan.uniqueId(),
                        targetClan.data().name(),
                        ClanRelationType.ALLIANCE,
                        null
                )
        );

        targetClan.relations().add(
                new DefaultClanRelation(
                        clan.uniqueId(),
                        clan.data().name(),
                        ClanRelationType.ALLIANCE,
                        null
                )
        );

        this.propagateTensions(targetClan, clan);
        this.propagateTensions(clan, targetClan);

        this.transactionManager.execute(() ->
                this.clanAllyStorage.createBatch(
                        List.of(ClanAllyEntry.from(clan, targetClan), ClanAllyEntry.from(targetClan, clan))
                )
        );

        return Response.success(ClanAllyAddResult.SUCCESS);
    }

    @Override
    public Response<ClanAllyRemoveResult> removeAllyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        final Clan clan = clanMember.clan();
        if (clan == targetClan) {
            return Response.failure(ClanAllyRemoveResult.SAME_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.MANAGE_DIPLOMACY)) {
            return Response.failure(ClanAllyRemoveResult.NO_PERMISSION);
        }

        if (!clan.relations().isAlly(targetClan.uniqueId())) {
            return Response.failure(ClanAllyRemoveResult.NOT_ALLIES);
        }

        if (!this.clanEventBus.callClanAllyRemoveEvent(clanMember, targetClan)) {
            return Response.failure(ClanAllyRemoveResult.CANCELLED);
        }

        clan.relations().remove(targetClan.uniqueId());
        targetClan.relations().remove(clan.uniqueId());

        this.cleanTensions(clan, targetClan);
        this.cleanTensions(targetClan, clan);

        this.transactionManager.execute(() ->
                this.clanAllyStorage.deleteBatch(
                        List.of(ClanAllyEntry.from(clan, targetClan), ClanAllyEntry.from(targetClan, clan))
                )
        );

        return Response.success(ClanAllyRemoveResult.SUCCESS);
    }

    @Override
    public Response<ClanEnemyAddResult> addEnemyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        final Clan clan = clanMember.clan();
        if (clan == targetClan) {
            return Response.failure(ClanEnemyAddResult.SAME_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.MANAGE_DIPLOMACY)) {
            return Response.failure(ClanEnemyAddResult.NO_PERMISSION);
        }

        final ClanRelations clanRelations = clan.relations();
        final ClanRelationType clanRelationType = clanRelations.getRelationType(targetClan.uniqueId());
        if (clanRelationType == ClanRelationType.ENEMY) {
            return Response.failure(ClanEnemyAddResult.ALREADY_ENEMIES);
        } else if (clanRelationType == ClanRelationType.ALLIANCE) {
            return Response.failure(ClanEnemyAddResult.IS_ALLY);
        } else if (clanRelationType == ClanRelationType.TENSION) {
            return Response.failure(ClanEnemyAddResult.IN_TENSION);
        }

        if (!this.clanEventBus.callClanEnemyAddEvent(clanMember, targetClan)) {
            return Response.failure(ClanEnemyAddResult.CANCELLED);
        }

        clanRelations.add(
                new DefaultClanRelation(
                        targetClan.uniqueId(),
                        targetClan.data().name(),
                        ClanRelationType.ENEMY,
                        null
                )
        );

        clanRelations.allies().forEach(ally -> {
            final Clan allyClan = this.clanCache.get(ally.clanUniqueId());
            if (allyClan != null) {
                allyClan.relations().add(
                        new DefaultClanRelation(
                                targetClan.uniqueId(),
                                targetClan.data().name(),
                                ClanRelationType.TENSION,
                                clan.uniqueId()
                        )
                );
            }

            this.clanEventBus.callClanTensionAddEvent(
                    ally.clanUniqueId(),
                    ally.clanName(),
                    allyClan,
                    targetClan,
                    clan
            );
        });

        this.transactionManager.execute(() -> this.clanEnemyStorage.create(ClanEnemyEntry.from(clan, targetClan)));
        return Response.success(ClanEnemyAddResult.SUCCESS);
    }

    @Override
    public Response<ClanEnemyRemoveResult> removeEnemyClan(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        final Clan clan = clanMember.clan();
        if (clan == targetClan) {
            return Response.failure(ClanEnemyRemoveResult.SAME_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.MANAGE_DIPLOMACY)) {
            return Response.failure(ClanEnemyRemoveResult.NO_PERMISSION);
        }

        final ClanRelations clanRelations = clan.relations();
        if (!clanRelations.isEnemy(targetClan.uniqueId())) {
            return Response.failure(ClanEnemyRemoveResult.NOT_ENEMIES);
        }

        if (!this.clanEventBus.callClanEnemyRemoveEvent(clanMember, targetClan)) {
            return Response.failure(ClanEnemyRemoveResult.CANCELLED);
        }

        clanRelations.remove(targetClan.uniqueId());
        clanRelations.allies().forEach(ally -> {
            final Clan allyClan = this.clanCache.get(ally.clanUniqueId());
            if (allyClan != null) {
                allyClan.relations().tensions().stream()
                        .filter(tension -> Objects.equals(tension.sourceClanUniqueId(), clan.uniqueId()) && Objects.equals(tension.clanUniqueId(), targetClan.uniqueId()))
                        .forEach(tension -> allyClan.relations().remove(tension.clanUniqueId()));
            }

            this.clanEventBus.callClanTensionRemoveEvent(
                    ally.clanUniqueId(),
                    ally.clanName(),
                    allyClan,
                    targetClan,
                    clan
            );
        });

        this.transactionManager.execute(() ->
                this.clanEnemyStorage.delete(clan.uniqueId(), targetClan.uniqueId())
        );

        return Response.success(ClanEnemyRemoveResult.SUCCESS);
    }

    private void propagateTensions(
            final @NotNull Clan source,
            final @NotNull Clan receiver
    ) {
        source.relations().enemies().forEach(enemy -> {
            if (receiver.relations().getRelationType(enemy.clanUniqueId()) == ClanRelationType.NEUTRAL) {
                receiver.relations().add(
                        new DefaultClanRelation(
                                enemy.clanUniqueId(),
                                enemy.clanName(),
                                ClanRelationType.TENSION,
                                source.uniqueId()
                        )
                );

                this.clanEventBus.callClanTensionAddEvent(
                        receiver.uniqueId(),
                        receiver.data().name(),
                        receiver,
                        this.clanCache.get(enemy.clanUniqueId()),
                        source
                );
            }
        });
    }

    private void cleanTensions(
            final @NotNull Clan clan,
            final @NotNull Clan source
    ) {
        clan.relations().tensions().stream()
                .filter(tension -> Objects.equals(tension.sourceClanUniqueId(), source.uniqueId()))
                .forEach(tension -> {
                    clan.relations().remove(tension.clanUniqueId());
                    this.clanEventBus.callClanTensionRemoveEvent(
                            clan.uniqueId(),
                            clan.data().name(),
                            clan,
                            this.clanCache.get(tension.clanUniqueId()),
                            source
                    );
                });
    }
}