package team.bytephoria.byteclans.spi.storage.entry;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;

import java.util.UUID;

public record ClanAllyEntry(
        UUID clanUniqueId,
        UUID allyClanUniqueId
) {

    @Contract("_, _ -> new")
    public static @NotNull ClanAllyEntry from(final @NotNull Clan clan, final @NotNull Clan ally) {
        return new ClanAllyEntry(clan.uniqueId(), ally.uniqueId());
    }

}
