package team.bytephoria.byteclans.api;

import java.util.Objects;
import java.util.UUID;

public record ClanRequestAlly(
        UUID senderUniqueId,
        UUID clanSenderUniqueId,
        UUID clanReceiverUniqueId
) {

    public ClanRequestAlly {
        Objects.requireNonNull(senderUniqueId);
        Objects.requireNonNull(clanSenderUniqueId);
        Objects.requireNonNull(clanReceiverUniqueId);
    }

}
