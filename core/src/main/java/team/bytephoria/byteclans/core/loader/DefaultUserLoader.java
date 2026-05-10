package team.bytephoria.byteclans.core.loader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.loader.ClanLoader;
import team.bytephoria.byteclans.spi.loader.UserLoader;
import team.bytephoria.byteclans.spi.storage.ClanMemberStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanMemberEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanMemberField;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultUserLoader implements UserLoader {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final ClanMemberStorage clanMemberStorage;
    private final ClanLoader clanLoader;

    public DefaultUserLoader(
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanMemberStorage clanMemberStorage,
            final @NotNull ClanLoader clanLoader
    ) {
        this.clanMemberCache = clanMemberCache;
        this.clanMemberStorage = clanMemberStorage;
        this.clanLoader = clanLoader;
    }

    @Override
    public @NotNull CompletableFuture<Optional<ClanMember>> load(final @NotNull ClanPlayer clanPlayer) {
        return this.clanMemberStorage.async()
                .findClanUniqueIdByUniqueId(clanPlayer.uniqueId())
                .thenCompose(clanUniqueIdOptional -> {
                    if (clanUniqueIdOptional.isEmpty()) {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }

                    final UUID clanUniqueId = clanUniqueIdOptional.get();
                    return this.clanLoader.load(clanUniqueId)
                            .thenApply(clan -> {
                                if (clan == null) {
                                    return Optional.empty();
                                }

                                final ClanMember clanMember = clan.getMemberByUniqueId(clanPlayer.uniqueId());
                                if (clanMember == null) {
                                    return Optional.empty();
                                }

                                clanMember.player(clanPlayer);
                                clanMember.data().lastSeenAt(Instant.now());

                                this.clanMemberStorage.update(
                                        ClanMemberEntry.fromNow(clanMember),
                                        ClanMemberField.LAST_SEEN_AT
                                );

                                this.clanMemberCache.add(clanMember);
                                return Optional.of(clanMember);
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
        if (clan.onlineAllMembers().size() == 1) {
            this.clanLoader.unload(clan.uniqueId());
        } else {
            clanMember.player(null);
        }

        return clanMember;
    }

}
