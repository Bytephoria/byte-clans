package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public interface ClanData {

    String name();
    String displayName();

    Instant createdAt();

    void name(final @NotNull String name);
    void displayName(final @NotNull String displayName);

}
