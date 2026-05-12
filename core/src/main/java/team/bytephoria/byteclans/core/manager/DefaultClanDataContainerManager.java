package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.manager.ClanDataContainerManager;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;

import java.util.concurrent.CompletableFuture;

public final class DefaultClanDataContainerManager implements ClanDataContainerManager {

    private final ClanStorage clanStorage;
    public DefaultClanDataContainerManager(final @NotNull ClanStorage clanStorage) {
        this.clanStorage = clanStorage;
    }

    @Override
    public CompletableFuture<Void> save(final @NotNull Clan clan) {
        return this.clanStorage.async().update(ClanEntry.from(clan), ClanField.PERSISTENT_DATA);
    }
}
