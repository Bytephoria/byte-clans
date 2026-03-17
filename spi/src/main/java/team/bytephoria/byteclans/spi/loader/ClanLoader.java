package team.bytephoria.byteclans.spi.loader;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanLoader {

    CompletableFuture<Clan> load(final @NotNull UUID clanUniqueId);
    Clan unload(final @NotNull UUID clanUniqueId);

}
