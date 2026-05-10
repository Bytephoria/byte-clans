package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.util.IntValue;

public interface ClanStatistics {

    @NotNull IntValue kills();
    @NotNull IntValue deaths();
    @NotNull IntValue killsStreak();
    @NotNull IntValue points();

    /** Calculated value, this is not a persistent data **/
    double kdr();

}
