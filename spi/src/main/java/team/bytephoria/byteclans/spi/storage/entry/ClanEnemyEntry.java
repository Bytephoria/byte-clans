package team.bytephoria.byteclans.spi.storage.entry;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;

import java.util.UUID;

public record ClanEnemyEntry(
        UUID clanUniqueId,
        UUID enemyClanUniqueId
) {

    @Contract("_, _ -> new")
    public static @NotNull ClanEnemyEntry from(final @NotNull Clan clan, final @NotNull Clan enemy) {
        return new ClanEnemyEntry(clan.uniqueId(), enemy.uniqueId());
    }

}