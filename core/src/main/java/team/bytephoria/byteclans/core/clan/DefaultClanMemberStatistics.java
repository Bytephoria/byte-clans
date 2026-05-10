package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMemberStatistics;
import team.bytephoria.byteclans.api.util.IntValue;

public final class DefaultClanMemberStatistics implements ClanMemberStatistics {

    private final IntValue kills;
    private final IntValue deaths;

    public DefaultClanMemberStatistics(
            final @NotNull IntValue kills,
            final @NotNull IntValue deaths
    ) {
        this.kills = kills;
        this.deaths = deaths;
    }

    public DefaultClanMemberStatistics(
            final int kills,
            final int deaths
    ) {
        this(new IntValue(kills), new IntValue(deaths));
    }

    public DefaultClanMemberStatistics() {
        this(0, 0);
    }

    @Override
    public @NotNull IntValue kills() {
        return this.kills;
    }

    @Override
    public @NotNull IntValue deaths() {
        return this.deaths;
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
