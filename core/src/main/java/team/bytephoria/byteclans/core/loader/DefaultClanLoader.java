package team.bytephoria.byteclans.core.loader;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.core.factory.ClanFactory;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.loader.ClanLoader;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.view.ClanView;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultClanLoader implements ClanLoader {

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    private final ClanStorage clanStorage;
    private final ClanFactory clanFactory;

    public DefaultClanLoader(
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanStorage clanStorage,
            final @NotNull ClanFactory clanFactory
    ) {
        this.clanCache = clanCache;
        this.clanMemberCache = clanMemberCache;
        this.clanStorage = clanStorage;
        this.clanFactory = clanFactory;
    }

    @Override
    public @NotNull CompletableFuture<Clan> load(final @NotNull UUID clanUniqueId) {
        final Clan cachedClan = this.clanCache.get(clanUniqueId);
        if (cachedClan != null) {
            return CompletableFuture.completedFuture(cachedClan);
        }

        return this.clanStorage.async()
                .findByUniqueId(clanUniqueId)
                .thenApply(optionalView -> {
                    if (optionalView.isEmpty()) {
                        return null;
                    }

                    final ClanView clanView = optionalView.get();
                    final Clan clan = this.clanFactory.create(clanView);
                    this.clanCache.add(clan);
                    return clan;

                });
    }

    @Override
    public @NotNull Clan unload(final @NotNull UUID clanUniqueId) {
        final Clan clan = this.clanCache.remove(clanUniqueId);
        clan.allMembers().forEach(this.clanMemberCache::remove);
        return clan;
    }
}
