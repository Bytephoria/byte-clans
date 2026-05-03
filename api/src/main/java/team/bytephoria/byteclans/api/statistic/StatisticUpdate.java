package team.bytephoria.byteclans.api.statistic;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.util.Operation;

import java.util.Objects;

public record StatisticUpdate(
        @NotNull StatisticType statisticType,
        int value,
        @NotNull Operation operation
) {

    public StatisticUpdate {
        Objects.requireNonNull(statisticType);
        Objects.requireNonNull(operation);
    }

}
