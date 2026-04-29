package team.bytephoria.byteclans.spi.storage.entry;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ClanEntry(
        UUID clanUniqueId,
        String ownerName,
        UUID ownerUniqueId,
        String clanName,
        String displayName,
        ClanInviteState clanInviteState,
        ClanPvPMode clanPvPMode,
        int maxMembers,
        int kills,
        int deaths,
        int killsStreak,
        Instant displayLastChangedAt,
        Instant createdAt
) {

    public ClanEntry {
        Objects.requireNonNull(clanUniqueId);
        Objects.requireNonNull(clanName);
    }

    @Contract("_ -> new")
    public static @NotNull ClanEntry from(final @NotNull Clan clan) {
        return new ClanEntry(
                clan.uniqueId(),
                clan.ownerData().name(),
                clan.ownerData().uniqueId(),
                clan.data().name(),
                clan.data().displayName(),
                clan.settings().inviteState(),
                clan.settings().pvpMode(),
                clan.settings().maxMembers(),
                clan.statistics().kills(),
                clan.statistics().deaths(),
                clan.statistics().killsStreak(),
                clan.data().displayLastChangedAt(),
                clan.data().createdAt()
        );
    }

}
