package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanMemberData;

import java.time.Instant;

public final class DefaultClanMemberData implements ClanMemberData {

    private final Instant joinedAt;
    private Instant lastSeenAt;

    public DefaultClanMemberData(
            final @NotNull Instant joinedAt,
            final @NotNull Instant lastSeenAt
    ) {
        this.joinedAt = joinedAt;
        this.lastSeenAt = lastSeenAt;
    }

    @Contract(" -> new")
    public static @NonNull DefaultClanMemberData now() {
        return new DefaultClanMemberData(Instant.now(), Instant.now());
    }

    @Override
    public Instant joinedAt() {
        return this.joinedAt;
    }

    @Override
    public Instant lastSeenAt() {
        return this.lastSeenAt;
    }

    @Override
    public void lastSeenAt(final @NotNull Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
