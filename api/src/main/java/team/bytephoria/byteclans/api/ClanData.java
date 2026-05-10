package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public interface ClanData {

    String name();
    String displayName();

    Instant createdAt();

    @Nullable Instant displayNameChangeAvailableAt();

    void name(final @NotNull String name);
    void displayName(final @NotNull String displayName);

    void displayNameChangeAvailableAt(final @Nullable Instant availableAt);

    boolean isDisplayNameInCooldown();

}