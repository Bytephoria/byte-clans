package team.bytephoria.byteclans.platform.spigot.listener.v1_20_4;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.DamageResult;
import team.bytephoria.byteclans.spi.processors.ClanCombatProcessor;

public final class V1_20_4EntityDamageByEntityListener implements Listener {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final ClanCombatProcessor combatProcessor;

    public V1_20_4EntityDamageByEntityListener(
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanCombatProcessor combatProcessor
    ) {
        this.clanMemberCache = clanMemberCache;
        this.combatProcessor = combatProcessor;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(final @NotNull EntityDamageByEntityEvent entityEvent) {
        if (!(entityEvent.getEntity() instanceof Player damagedPlayer)) {
            return;
        }

        final DamageSource damageSource = entityEvent.getDamageSource();
        if (!(damageSource.getCausingEntity() instanceof Player damagerPlayer)) {
            return;
        }

        final ClanMember damagedClanMember = this.clanMemberCache.get(damagedPlayer.getUniqueId());
        if (damagedClanMember == null) {
            return;
        }

        final ClanMember damagerClanMember = this.clanMemberCache.get(damagerPlayer.getUniqueId());
        if (damagerClanMember == null) {
            return;
        }

        final DamageResult damageResult = this.combatProcessor.process(
                damagerClanMember,
                damagedClanMember,
                entityEvent.getDamage(),
                damagedPlayer.getHealth()
        );

        switch (damageResult) {
            case CANCEL, SAFE -> entityEvent.setCancelled(true);
        }

    }

}
