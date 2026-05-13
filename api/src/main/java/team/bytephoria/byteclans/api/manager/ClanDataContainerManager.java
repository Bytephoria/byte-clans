package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;

import java.util.concurrent.CompletableFuture;

public interface ClanDataContainerManager {

    CompletableFuture<Void> save(final @NotNull Clan clan);

}
