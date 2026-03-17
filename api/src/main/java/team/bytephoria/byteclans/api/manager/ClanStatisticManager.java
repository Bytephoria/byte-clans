package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.util.response.Response;

public interface ClanStatisticManager {

    Response<Integer> addKills(final @NotNull Clan clan, final int amount);

    Response<Integer> addDeaths(final @NotNull Clan clan, final int amount);

    void addKillsAndKs(final @NotNull Clan clan, final int kills, final int ks);

    Response<Integer> addDeathAndResetKs(final @NotNull Clan clan, final int amount);

    void resetKs(final @NotNull Clan clan);

}
