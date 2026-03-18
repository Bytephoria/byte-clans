package team.bytephoria.byteclans.platform.spigot.listener.v1_20_3;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.DamageResult;
import team.bytephoria.byteclans.spi.processors.ClanCombatProcessor;

public final class V1_20_3EntityDamageByEntityListener implements Listener {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final ClanCombatProcessor combatProcessor;

    public V1_20_3EntityDamageByEntityListener(
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

        final Entity damager = entityEvent.getDamager();
        final Player damagerPlayer;
        if (damager instanceof Player player) {
            damagerPlayer = player;
        } else if (damager instanceof Projectile projectile) {
            final ProjectileSource projectileSource = projectile.getShooter();
            if (projectileSource instanceof Player player) {
                damagerPlayer = player;
            } else {
                return;
            }
        } else {
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
