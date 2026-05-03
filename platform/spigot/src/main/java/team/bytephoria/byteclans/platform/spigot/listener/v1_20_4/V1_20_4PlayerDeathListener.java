package team.bytephoria.byteclans.platform.spigot.listener.v1_20_4;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.manager.ClanManager;
import team.bytephoria.byteclans.api.manager.ClanStatisticManager;
import team.bytephoria.byteclans.api.util.IntValue;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;

public final class V1_20_4PlayerDeathListener implements Listener {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final ClanManager clanManager;
    private final ClanStatisticManager clanStatisticManager;
    private final ClanGlobalSettings clanGlobalSettings;

    public V1_20_4PlayerDeathListener(
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanManager clanManager,
            final @NotNull ClanStatisticManager clanStatisticManager,
            final @NotNull ClanGlobalSettings clanGlobalSettings
    ) {
        this.clanMemberCache = clanMemberCache;
        this.clanManager = clanManager;
        this.clanStatisticManager = clanStatisticManager;
        this.clanGlobalSettings = clanGlobalSettings;
    }

    @EventHandler
    public void onPlayerDeath(final @NotNull PlayerDeathEvent deathEvent) {
        final Player player = deathEvent.getEntity();
        final DamageSource damageSource = deathEvent.getDamageSource();

        if (damageSource.getCausingEntity() instanceof Player killer && player != killer) {
            this.clanMemberCache.getIfPresent(killer.getUniqueId())
                    .ifPresent(clanMember -> {
                        this.clanStatisticManager.addKillsAndKs(clanMember.clan(), 1, 1);
                        this.clanManager.updatePoints(clanMember.clan(), this.clanGlobalSettings.pointsPerKill(), IntValue.Operation.SUM);
                    });
        }

        this.clanMemberCache.getIfPresent(player.getUniqueId())
                .ifPresent(clanMember -> {
                    this.clanStatisticManager.addDeathAndResetKs(clanMember.clan(), 1);
                    this.clanManager.updatePoints(clanMember.clan(), this.clanGlobalSettings.pointsPerDeath(), IntValue.Operation.SUM);
                });

    }

}
