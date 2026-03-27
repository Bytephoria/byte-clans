package team.bytephoria.byteclans.spi.storage.view;

import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.ClanRelationType;

import java.time.Instant;
import java.util.UUID;

public record ClanRelationView(
        UUID senderClanUniqueId,
        UUID receiverClanUniqueId,
        String receiverClanName,
        ClanRelationType relationType,
        @Nullable UUID sourceClanUniqueId,
        Instant createdAt
) {
}
