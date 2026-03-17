package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public interface ClanMemberData {

    Instant joinedAt();
    Instant lastSeenAt();

    void lastSeenAt(final @NotNull Instant lastSeenAt);

}
