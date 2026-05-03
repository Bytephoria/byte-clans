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

    public DefaultClanStatistics(
            final @NotNull IntValue kills,
            final @NotNull IntValue deaths,
            final @NotNull IntValue killsStreak
    ) {
        this.kills = kills;
        this.deaths = deaths;
        this.killsStreak = killsStreak;
    }

    public DefaultClanStatistics(
            final int kills,
            final int deaths,
            final int killsStreak
    ) {
        this(new IntValue(kills), new IntValue(deaths), new IntValue(killsStreak));
    }

    public DefaultClanStatistics() {
        this(0, 0, 0);
    }

    @Contract(" -> new")
    public static @NonNull DefaultClanStatistics allZero() {
        return new DefaultClanStatistics(0, 0, 0);
    }

    @Override
    public IntValue kills() {
        return this.kills;
    }

    @Override
    public IntValue deaths() {
        return this.deaths;
    }

    @Override
    public IntValue killsStreak() {
        return this.killsStreak;
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
