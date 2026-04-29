package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.ClanData;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class DefaultClanData implements ClanData {

    private String name;
    private String displayName;
    private Instant displayLastChangedAt;

    private final Instant createdAt;

    public DefaultClanData(
            final String name,
            final @NotNull String displayName,
            final @Nullable Instant displayLastChangedAt,
            final Instant createdAt
    ) {
        this.name = Objects.requireNonNull(name);
        this.displayName = Objects.requireNonNull(displayName);
        this.displayLastChangedAt = displayLastChangedAt;
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
    public @Nullable Instant displayLastChangedAt() {
        return this.displayLastChangedAt;
    }

    @Override
    public void name(final @NotNull String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public void displayName(final @NotNull String displayName) {
        this.displayName = Objects.requireNonNull(displayName);
    }

    @Override
    public void displayLastChangedAt(final @NotNull Instant lastChangedAt) {
        this.displayLastChangedAt = Objects.requireNonNull(lastChangedAt);
    }

    @Override
    public boolean isDisplayInCooldown(final @NotNull Duration duration) {
        return this.displayLastChangedAt != null && this.displayLastChangedAt.plus(duration).isAfter(Instant.now());
    }

    @Override
    public boolean isDisplayInCooldown(final int amount, @NotNull TimeUnit timeUnit) {
        return this.isDisplayInCooldown(Duration.of(amount, timeUnit.toChronoUnit()));
    }

}
