package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanData;

import java.time.Instant;
import java.util.Objects;

public final class DefaultClanData implements ClanData {

    private String name;
    private String displayName;

    private final Instant createdAt;

    public DefaultClanData(
            final String name,
            final @NotNull String displayName,
            final Instant createdAt
    ) {
        this.name = Objects.requireNonNull(name);
        this.displayName = Objects.requireNonNull(displayName);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String displayName() {
        return this.displayName;
    }

    @Override
    public Instant createdAt() {
        return this.createdAt;
    }

    @Override
    public void name(final @NotNull String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public void displayName(final @NotNull String displayName) {
        this.displayName = Objects.requireNonNull(displayName);
    }
}
