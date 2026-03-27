package team.bytephoria.byteclans.spi.storage.entry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanRelationType;

import java.util.UUID;

public record ClanRelationEntry(
        UUID senderUniqueId,
        UUID receiverUniqueId,
        ClanRelationType relationType,
        @Nullable UUID sourceUniqueId
) {

    public static @NotNull ClanRelationEntry from(
            final @Nullable UUID senderClanUniqueId,
            final @NotNull Clan receiverClan,
            final @NotNull ClanRelationType clanRelationType,
            final @Nullable UUID sourceUniqueId
    ) {
        return new ClanRelationEntry(senderClanUniqueId, receiverClan.uniqueId(), clanRelationType, sourceUniqueId);
    }

    public static @NotNull ClanRelationEntry from(
            final @NotNull Clan senderClan,
            final @NotNull Clan receiverClan,
            final @NotNull ClanRelationType clanRelationType,
            final @Nullable UUID sourceUniqueId
    ) {
        return from(senderClan.uniqueId(), receiverClan, clanRelationType, sourceUniqueId);
    }

    public static @NotNull ClanRelationEntry from(
            final @NotNull Clan senderClan,
            final @NotNull Clan receiverClan,
            final @NotNull ClanRelationType clanRelationType
    ) {
        return from(senderClan, receiverClan, clanRelationType, null);
    }


}
