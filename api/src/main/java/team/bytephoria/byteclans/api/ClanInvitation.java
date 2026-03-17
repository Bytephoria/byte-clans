package team.bytephoria.byteclans.api;

import java.util.Objects;
import java.util.UUID;

public record ClanInvitation(
        UUID senderUniqueId,
        UUID targetUniqueId,
        UUID clanUniqueId
) {

    public ClanInvitation {
        Objects.requireNonNull(senderUniqueId);
        Objects.requireNonNull(targetUniqueId);
        Objects.requireNonNull(clanUniqueId);
    }

}
