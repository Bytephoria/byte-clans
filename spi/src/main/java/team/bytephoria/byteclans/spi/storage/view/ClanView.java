package team.bytephoria.byteclans.spi.storage.view;

import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ClanView(
        UUID clanUniqueId,
        String ownerName,
        UUID ownerUniqueId,
        String clanName,
        String clanDisplayName,
        ClanInviteState clanInviteState,
        ClanPvPMode clanPvPMode,
        int maxMembers,
        int kills,
        int deaths,
        int killsStreak,
        Instant createdAt
) {

    public ClanView {
        Objects.requireNonNull(clanUniqueId);
        Objects.requireNonNull(ownerUniqueId);
    }


}
