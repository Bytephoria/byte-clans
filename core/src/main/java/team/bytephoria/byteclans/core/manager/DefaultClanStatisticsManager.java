package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanStatistics;
import team.bytephoria.byteclans.api.manager.ClanStatisticManager;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;

public final class DefaultClanStatisticsManager implements ClanStatisticManager {

    private final ClanStorage clanStorage;
    public DefaultClanStatisticsManager(final @NotNull ClanStorage clanStorage) {
        this.clanStorage = clanStorage;
    }

    @Override
    public @NotNull Response<Integer> addKills(final @NotNull Clan clan, final int amount) {
        final ClanStatistics clanStatistics = clan.statistics();
        final int finalAmount = clanStatistics.kills() + amount;
        clanStatistics.kills(finalAmount);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.KILLS);
        return Response.success(finalAmount);
    }

    @Override
    public @NotNull Response<Integer> addDeaths(final @NotNull Clan clan, final int amount) {
        final ClanStatistics clanStatistics = clan.statistics();
        final int finalAmount = clanStatistics.deaths() + amount;

        clanStatistics.deaths(finalAmount);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.DEATHS);
        return Response.success(finalAmount);
    }

    @Override
    public void addKillsAndKs(final @NotNull Clan clan, final int kills, final int ks) {
        final ClanStatistics clanStatistics = clan.statistics();
        clanStatistics.kills(clanStatistics.kills() + kills);
        clanStatistics.killsStreak(clanStatistics.killsStreak() + ks);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.KILLS, ClanField.KILLS_STREAK);
    }

    @Override
    public @NotNull Response<Integer> addDeathAndResetKs(final @NotNull Clan clan, final int amount) {
        final ClanStatistics clanStatistics = clan.statistics();
        final int finalAmount = clanStatistics.deaths() + amount;

        clanStatistics.deaths(finalAmount);
        if (clanStatistics.killsStreak() > 0) {
            clanStatistics.killsStreak(0);
            this.clanStorage.async().update(ClanEntry.from(clan), ClanField.DEATHS, ClanField.KILLS_STREAK);
        } else {
            this.clanStorage.async().update(ClanEntry.from(clan), ClanField.DEATHS);
        }

        return Response.success(finalAmount);
    }

    @Override
    public void resetKs(final @NotNull Clan clan) {
        final ClanStatistics clanStatistics = clan.statistics();
        if (clanStatistics.killsStreak() == 0) {
            return;
        }

        clanStatistics.killsStreak(0);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.KILLS_STREAK);
    }
}
