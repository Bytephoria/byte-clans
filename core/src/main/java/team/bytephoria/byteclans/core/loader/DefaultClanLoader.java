package team.bytephoria.byteclans.core.loader;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanRelationType;
import team.bytephoria.byteclans.api.registry.ClanRoleRegistry;
import team.bytephoria.byteclans.core.clan.DefaultClanMember;
import team.bytephoria.byteclans.core.clan.DefaultClanRelation;
import team.bytephoria.byteclans.core.factory.ClanFactory;
import team.bytephoria.byteclans.core.factory.ClanMemberFactory;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.loader.ClanLoader;
import team.bytephoria.byteclans.spi.storage.ClanAllyStorage;
import team.bytephoria.byteclans.spi.storage.ClanEnemyStorage;
import team.bytephoria.byteclans.spi.storage.ClanMemberStorage;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.view.ClanAllyView;
import team.bytephoria.byteclans.spi.storage.view.ClanEnemyView;
import team.bytephoria.byteclans.spi.storage.view.ClanMemberView;
import team.bytephoria.byteclans.spi.storage.view.ClanTensionView;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultClanLoader implements ClanLoader {

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    private final ClanStorage clanStorage;
    private final ClanFactory clanFactory;

    private final ClanAllyStorage clanAllyStorage;
    private final ClanEnemyStorage clanEnemyStorage;

    private final ClanMemberStorage clanMemberStorage;
    private final ClanMemberFactory clanMemberFactory;
    private final ClanRoleRegistry clanRoleRegistry;

    public DefaultClanLoader(
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanStorage clanStorage,
            final @NotNull ClanFactory clanFactory,
            final @NotNull ClanAllyStorage clanAllyStorage,
            final @NotNull ClanEnemyStorage clanEnemyStorage,
            final @NotNull ClanMemberStorage clanMemberStorage,
            final @NotNull ClanMemberFactory clanMemberFactory,
            final @NotNull ClanRoleRegistry clanRoleRegistry
    ) {
        this.clanCache = clanCache;
        this.clanMemberCache = clanMemberCache;
        this.clanStorage = clanStorage;
        this.clanFactory = clanFactory;
        this.clanAllyStorage = clanAllyStorage;
        this.clanEnemyStorage = clanEnemyStorage;
        this.clanMemberStorage = clanMemberStorage;
        this.clanMemberFactory = clanMemberFactory;
        this.clanRoleRegistry = clanRoleRegistry;
    }

    @Override
    public @NotNull CompletableFuture<Clan> load(final @NotNull UUID clanUniqueId) {
        final Clan cachedClan = this.clanCache.get(clanUniqueId);
        if (cachedClan != null) {
            return CompletableFuture.completedFuture(cachedClan);
        }

        return this.clanStorage.async()
                .findByUniqueId(clanUniqueId)
                .thenCompose(optionalView -> {
                    if (optionalView.isEmpty()) {
                        return CompletableFuture.completedFuture(null);
                    }

                    final Clan clan = this.clanFactory.create(optionalView.get());
                    final CompletableFuture<Collection<ClanAllyView>> alliesFuture =
                            this.clanAllyStorage.async().findByClanUniqueId(clanUniqueId);

                    final CompletableFuture<Collection<ClanEnemyView>> enemiesFuture =
                            this.clanEnemyStorage.async().findByClanUniqueId(clanUniqueId);

                    final CompletableFuture<Collection<ClanTensionView>> tensionsFuture =
                            this.clanEnemyStorage.async().findTensionsByClanUniqueId(clanUniqueId);

                    return CompletableFuture.allOf(alliesFuture, enemiesFuture, tensionsFuture)
                            .thenApply(ignored -> {
                                for (final ClanAllyView view : alliesFuture.join()) {
                                    clan.relations().add(new DefaultClanRelation(
                                            view.clanUniqueId(),
                                            view.clanName(),
                                            ClanRelationType.ALLIANCE,
                                            null
                                    ));
                                }

                                for (final ClanEnemyView view : enemiesFuture.join()) {
                                    clan.relations().add(new DefaultClanRelation(
                                            view.clanUniqueId(),
                                            view.clanName(),
                                            ClanRelationType.ENEMY,
                                            null
                                    ));
                                }

                                for (final ClanTensionView view : tensionsFuture.join()) {
                                    clan.relations().add(new DefaultClanRelation(
                                            view.enemyClanUniqueId(),
                                            view.enemyClanName(),
                                            ClanRelationType.TENSION,
                                            view.sourceClanUniqueId()
                                    ));
                                }

                                this.clanCache.add(clan);
                                return clan;
                            });
                })
                .thenCompose(clan -> {
                    if (clan == null) {
                        return CompletableFuture.completedFuture(null);
                    }

                    return this.clanMemberStorage.async()
                            .findAllByIdentity(clan)
                            .thenApply(clanMemberViews -> {
                                for (final ClanMemberView clanMemberView : clanMemberViews) {
                                    final DefaultClanMember clanMember = this.clanMemberFactory.create(
                                            clanMemberView,
                                            clan,
                                            this.clanRoleRegistry
                                    );

                                    if (clan.isOwner(clanMemberView.memberUniqueId())) {
                                        clan.ownerMember(clanMember);
                                        continue;
                                    }

                                    clan.addMember(clanMember);
                                }

                                return clan;
                            });
                });
    }

    @Override
    public @NotNull Clan unload(final @NotNull UUID clanUniqueId) {
        final Clan clan = this.clanCache.remove(clanUniqueId);
        clan.allOnlineMembers().forEach(this.clanMemberCache::remove);
        return clan;
    }
}
