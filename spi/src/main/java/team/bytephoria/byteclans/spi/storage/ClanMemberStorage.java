package team.bytephoria.byteclans.spi.storage;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.util.Identity;
import team.bytephoria.byteclans.spi.storage.entry.ClanMemberEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanMemberField;
import team.bytephoria.byteclans.spi.storage.view.ClanMemberView;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanMemberStorage {

    int countMembers();

    void create(final @NotNull ClanMemberEntry clanMemberEntry);

    void update(final @NotNull ClanMemberEntry clanMemberEntry);

    void update(final @NotNull ClanMemberEntry clanMemberEntry, final @NotNull ClanMemberField @NotNull ... fields);

    void deleteByUniqueId(final @NotNull UUID uniqueId);

    Optional<ClanMemberView> findByUniqueId(final @NotNull UUID uniqueId);

    Optional<ClanMemberView> findByIdentity(final @NotNull Identity identity);

    Async async();

    interface Async {
        CompletableFuture<Void> create(final @NotNull ClanMemberEntry clanMemberEntry);

        CompletableFuture<Void> update(final @NotNull ClanMemberEntry clanMemberEntry);

        CompletableFuture<Void> update(final @NotNull ClanMemberEntry clanMemberEntry, final @NotNull ClanMemberField @NotNull ... fields);

        CompletableFuture<Void> deleteByUniqueId(final @NotNull UUID uniqueId);

        CompletableFuture<Optional<ClanMemberView>> findByUniqueId(final @NotNull UUID uniqueId);

        CompletableFuture<Optional<ClanMemberView>> findByIdentity(final @NotNull Identity identity);
    }


}