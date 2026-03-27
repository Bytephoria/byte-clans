package team.bytephoria.byteclans.spi.storage;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.spi.storage.entry.ClanAllyEntry;
import team.bytephoria.byteclans.spi.storage.view.ClanAllyView;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanAllyStorage {

    Collection<ClanAllyView> findByClanUniqueId(final @NotNull UUID clanUniqueId);

    void createBatch(final @NotNull Collection<ClanAllyEntry> entries);

    void deleteBatch(final @NotNull Collection<ClanAllyEntry> entries);

    Async async();

    interface Async {
        CompletableFuture<Collection<ClanAllyView>> findByClanUniqueId(final @NotNull UUID clanUniqueId);

        CompletableFuture<Void> createBatch(final @NotNull Collection<ClanAllyEntry> entries);

        CompletableFuture<Void> deleteBatch(final @NotNull Collection<ClanAllyEntry> entries);
    }

}