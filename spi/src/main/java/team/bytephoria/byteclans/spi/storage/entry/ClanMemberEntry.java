package team.bytephoria.byteclans.spi.storage.entry;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ClanMemberEntry(
        UUID memberUniqueId,
        UUID clanUniqueId,
        String memberName,
        String roleId,
        Instant joinedAt,
        Instant lastSeenAt
) {

    public ClanMemberEntry {
        Objects.requireNonNull(memberUniqueId);
        //Objects.requireNonNull(clanUniqueId);
    }

    @Contract("_ -> new")
    public static @NotNull ClanMemberEntry from(final @NotNull ClanMember clanMember) {
        return new ClanMemberEntry(
                clanMember.uniqueId(),
                clanMember.clan().uniqueId(),
                clanMember.name(),
                clanMember.role().id(),
                clanMember.data().joinedAt(),
                clanMember.data().lastSeenAt()
        );
    }

    public static @NotNull ClanMemberEntry fromNow(final @NotNull ClanMember clanMember) {
        return new ClanMemberEntry(
                clanMember.uniqueId(),
                clanMember.clan().uniqueId(),
                clanMember.name(),
                clanMember.role().id(),
                clanMember.data().joinedAt(),
                Instant.now()
        );
    }

}
