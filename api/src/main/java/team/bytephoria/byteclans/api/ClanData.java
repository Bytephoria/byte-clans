package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public interface ClanData {

    String name();
    String displayName();

    Instant createdAt();

    @Nullable Instant displayLastChangedAt();

    void name(final @NotNull String name);
    void displayName(final @NotNull String displayName);

    void displayLastChangedAt(final @NotNull Instant lastChangedAt);

    default void displayLastChangedAtNow() {
        this.displayLastChangedAt(Instant.now());
    }

    boolean isDisplayInCooldown(final @NotNull Duration duration);

    boolean isDisplayInCooldown(final int amount, final @NotNull TimeUnit timeUnit);

}
