package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanStatistics;
import team.bytephoria.byteclans.api.util.IntValue;

public final class DefaultClanStatistics implements ClanStatistics {

    private final IntValue kills;
    private final IntValue deaths;
    private final IntValue killsStreak;
    private final IntValue points;

    public DefaultClanStatistics(
            final @NotNull IntValue kills,
            final @NotNull IntValue deaths,
            final @NotNull IntValue killsStreak,
            final @NotNull IntValue points
    ) {
        this.kills = kills;
        this.deaths = deaths;
        this.killsStreak = killsStreak;
        this.points = points;
    }

    public DefaultClanStatistics(
            final int kills,
            final int deaths,
            final int killsStreak,
            final int points
    ) {
        this(new IntValue(kills), new IntValue(deaths), new IntValue(killsStreak), new IntValue(points));
    }

    public DefaultClanStatistics() {
        this(0, 0, 0, 0);
    }

    @Contract(" -> new")
    public static @NonNull DefaultClanStatistics allZero() {
        return new DefaultClanStatistics(0, 0, 0, 0);
    }

    @Override
    public @NonNull IntValue kills() {
        return this.kills;
    }

    @Override
    public @NonNull IntValue deaths() {
        return this.deaths;
    }

    @Override
    public @NonNull IntValue killsStreak() {
        return this.killsStreak;
    }

    @Override
    public @NotNull IntValue points() {
        return this.points;
    }

    @Override
    public double kdr() {
        final int kills = this.kills.value();
        final int deaths = this.deaths.value();

        if (deaths == 0) {
            return kills;
        }

        return (double) kills / deaths;
    }

}
