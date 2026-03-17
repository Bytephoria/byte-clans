package team.bytephoria.byteclans.core.loader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.registry.ClanRoleRegistry;
import team.bytephoria.byteclans.core.clan.DefaultClanMember;
import team.bytephoria.byteclans.core.factory.ClanMemberFactory;
import team.bytephoria.byteclans.core.registry.DefaultClanRoleRegistry;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.loader.ClanLoader;
import team.bytephoria.byteclans.spi.loader.UserLoader;
import team.bytephoria.byteclans.spi.storage.ClanMemberStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanMemberEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanMemberField;
import team.bytephoria.byteclans.spi.storage.view.ClanMemberView;

import java.util.concurrent.CompletableFuture;

public final class DefaultUserLoader implements UserLoader {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final ClanMemberStorage clanMemberStorage;

    private final ClanMemberFactory clanMemberFactory;
    private final ClanRoleRegistry clanRoleRegistry;
    private final ClanLoader clanLoader;

    public DefaultUserLoader(
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanMemberStorage clanMemberStorage,
            final @NotNull ClanMemberFactory clanMemberFactory,
            final @NotNull DefaultClanRoleRegistry clanRoleRegistry,
            final @NotNull ClanLoader clanLoader
    ) {
        this.clanMemberCache = clanMemberCache;
        this.clanMemberStorage = clanMemberStorage;
        this.clanMemberFactory = clanMemberFactory;
        this.clanRoleRegistry = clanRoleRegistry;
        this.clanLoader = clanLoader;
    }

    @Override
    public @NotNull CompletableFuture<ClanMember> load(final @NotNull ClanPlayer clanPlayer) {
        return this.clanMemberStorage.async()
                .findByIdentity(clanPlayer)
                .thenCompose(optionalView -> {
                    if (optionalView.isEmpty()) {
                        return CompletableFuture.completedFuture(null);
                    }

                    final ClanMemberView clanMemberView = optionalView.get();
                    return this.clanLoader.load(clanMemberView.clanUniqueId())
                            .thenApply(clan -> {
                                final DefaultClanMember clanMember = this.clanMemberFactory.create(clanMemberView, clan, this.clanRoleRegistry, clanPlayer);
                                if (clanMember.uniqueId().equals(clan.ownerData().uniqueId())) {
                                    clan.ownerMember(clanMember);
                                } else {
                                    clan.addMember(clanMember);
                                }

                                this.clanMemberStorage.update(ClanMemberEntry.fromNow(clanMember), ClanMemberField.LAST_SEEN_AT);
                                this.clanMemberCache.add(clanMember);
                                return clanMember;
                            });
                });
    }

    @Override
    public @Nullable ClanMember unload(final @NotNull ClanPlayer clanPlayer) {
        final ClanMember clanMember = this.clanMemberCache.remove(clanPlayer);
        if (clanMember == null) {
            return null;
        }

        final Clan clan = clanMember.clan();
        if (clan.allMembers().size() == 1) {
            this.clanLoader.unload(clan.uniqueId());
        } else {
            clan.removeMemberByUniqueId(clanMember.uniqueId());
        }

        return clanMember;
    }

}
