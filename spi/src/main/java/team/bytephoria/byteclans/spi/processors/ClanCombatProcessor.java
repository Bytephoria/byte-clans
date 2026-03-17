package team.bytephoria.byteclans.spi.processors;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.spi.DamageResult;

public interface ClanCombatProcessor {

    DamageResult process(
            final @NotNull ClanMember damager,
            final @NotNull ClanMember damaged,
            final double damage,
            final double health
    );

}
