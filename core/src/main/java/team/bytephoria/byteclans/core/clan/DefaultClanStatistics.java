package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanStatistics;

public final class DefaultClanStatistics implements ClanStatistics {

    private int kills;
    private int deaths;
    private int killsStreak;

    public DefaultClanStatistics(
            final int kills,
            final int deaths,
            final int killsStreak
    ) {
        this.kills = kills;
        this.deaths = deaths;
        this.killsStreak = killsStreak;
    }

    @Contract(" -> new")
    public static @NonNull DefaultClanStatistics allZero() {
        return new DefaultClanStatistics(0, 0, 0);
    }

    @Override
    public int kills() {
        return this.kills;
    }

    @Override
    public int deaths() {
        return this.deaths;
    }

    @Override
    public int killsStreak() {
        return this.killsStreak;
    }

    @Override
    public double kdr() {
        final int kills = this.kills;
        final int deaths = this.deaths;

        if (deaths == 0) {
            return kills;
        }

        return (double) kills / deaths;
    }

    @Override
    public void kills(final int kills) {
        this.kills = kills;
    }

    @Override
    public void deaths(final int deaths) {
        this.deaths = deaths;
    }

    @Override
    public void killsStreak(final int killsStreak) {
        this.killsStreak = killsStreak;
    }
}
