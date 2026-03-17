package team.bytephoria.byteclans.spi.storage;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;
import team.bytephoria.byteclans.spi.storage.view.ClanView;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanStorage {

    int countClans();

    void create(final @NotNull ClanEntry clanEntry);

    void update(final @NotNull ClanEntry clanEntry);

    void update(final @NotNull ClanEntry clanEntry, final @NotNull ClanField @NotNull... fields);

    void deleteByUniqueId(final @NotNull UUID uniqueId);

    boolean existsByUniqueId(final @NotNull UUID uniqueId);

    Optional<ClanView> findByUniqueId(final @NotNull UUID uniqueId);

    Async async();

    interface Async {

        CompletableFuture<Void> create(final @NotNull ClanEntry clanEntry);

        CompletableFuture<Void> update(final @NotNull ClanEntry clanEntry);

        CompletableFuture<Void> update(final @NotNull ClanEntry clanEntry, final @NotNull ClanField @NotNull ... fields);

        CompletableFuture<Void> deleteByUniqueId(final @NotNull UUID uniqueId);
        CompletableFuture<Boolean> existsByUniqueId(final @NotNull UUID uniqueId);
        CompletableFuture<Optional<ClanView>> findByUniqueId(final @NotNull UUID uniqueId);

    }

}
