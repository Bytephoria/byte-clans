package team.bytephoria.byteclans.spi.loader;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserLoader {

    CompletableFuture<Optional<ClanMember>> load(final @NotNull ClanPlayer clanPlayer);

    ClanMember unload(final @NotNull ClanPlayer clanPlayer);

}
