package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.util.IntValue;

public interface ClanMemberStatistics {

    @NotNull IntValue kills();
    @NotNull IntValue deaths();

    /** Calculated value, this is not a persistent data **/
    double kdr();

}
