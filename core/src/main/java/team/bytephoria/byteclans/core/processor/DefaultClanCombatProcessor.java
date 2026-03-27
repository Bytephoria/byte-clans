package team.bytephoria.byteclans.core.processor;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.spi.DamageResult;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.processors.ClanCombatProcessor;

public final class DefaultClanCombatProcessor implements ClanCombatProcessor {

    private final ClanEventBus eventBus;
    public DefaultClanCombatProcessor(final @NotNull ClanEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public DamageResult process(
            final @NotNull ClanMember damagerClanMember,
            final @NotNull ClanMember damagedClanMember,
            final double damage,
            final double health
    ) {

        final Clan clan = damagerClanMember.clan();
        final Clan targetClan = damagedClanMember.clan();

        if (clan != targetClan && !clan.relations().isAlly(targetClan.uniqueId())) {
            return DamageResult.ALLOW;
        }

        if (!this.eventBus.callMemberDamage(damagerClanMember, damagedClanMember)) {
            return DamageResult.CANCEL;
        }

        return switch (clan.settings().pvpMode()) {
            case FRIENDLY_FIRE -> DamageResult.ALLOW;
            case NO_DAMAGE -> DamageResult.CANCEL;
            case SAFE_DAMAGE -> damage >= health ? DamageResult.SAFE : DamageResult.ALLOW;
        };
    }

}
