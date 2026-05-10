package team.bytephoria.byteclans.spi.storage.view;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ClanMemberView(
        UUID clanUniqueId,
        UUID memberUniqueId,
        String memberName,
        String roleId,
        int kills,
        int deaths,
        Instant joinedAt,
        Instant lastSeenAt
) {

    public ClanMemberView {
        Objects.requireNonNull(clanUniqueId);
        Objects.requireNonNull(memberUniqueId);
    }


}
