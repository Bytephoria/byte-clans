package team.bytephoria.byteclans.api;

import team.bytephoria.byteclans.api.util.IntValue;

public interface ClanStatistics {

    IntValue kills();
    IntValue deaths();
    IntValue killsStreak();

    /** Calculated value, this is not a persistent data **/
    double kdr();

}
