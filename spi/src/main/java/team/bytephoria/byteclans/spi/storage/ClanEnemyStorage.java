package team.bytephoria.byteclans.spi.storage;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.spi.storage.entry.ClanEnemyEntry;
import team.bytephoria.byteclans.spi.storage.view.ClanEnemyView;
import team.bytephoria.byteclans.spi.storage.view.ClanTensionView;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanEnemyStorage {

    Collection<ClanEnemyView> findByClanUniqueId(final @NotNull UUID clanUniqueId);

    Collection<ClanTensionView> findTensionsByClanUniqueId(final @NotNull UUID clanUniqueId);

    void create(final @NotNull ClanEnemyEntry entry);

    void delete(final @NotNull UUID clanUniqueId, final @NotNull UUID enemyClanUniqueId);

    Async async();

    interface Async {
        CompletableFuture<Collection<ClanEnemyView>> findByClanUniqueId(final @NotNull UUID clanUniqueId);

        CompletableFuture<Void> create(final @NotNull ClanEnemyEntry entry);

        CompletableFuture<Void> delete(final @NotNull UUID clanUniqueId, final @NotNull UUID enemyClanUniqueId);

        CompletableFuture<Collection<ClanTensionView>> findTensionsByClanUniqueId(final @NotNull UUID clanUniqueId);

    }

}