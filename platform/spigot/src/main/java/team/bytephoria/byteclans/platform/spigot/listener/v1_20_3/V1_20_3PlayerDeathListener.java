package team.bytephoria.byteclans.platform.spigot.listener.v1_20_3;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.manager.ClanManager;
import team.bytephoria.byteclans.api.manager.ClanStatisticManager;
import team.bytephoria.byteclans.api.statistic.StatisticType;
import team.bytephoria.byteclans.api.statistic.StatisticUpdate;
import team.bytephoria.byteclans.api.util.Operation;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;

import java.util.List;

public final class V1_20_3PlayerDeathListener implements Listener {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final ClanManager clanManager;
    private final ClanStatisticManager clanStatisticManager;
    private final ClanGlobalSettings clanGlobalSettings;

    public V1_20_3PlayerDeathListener(
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
        final Player killer = player.getKiller();

        if (killer != null && player != killer) {
            this.clanMemberCache.getIfPresent(killer.getUniqueId())
                    .ifPresent(clanMember -> {
                        this.clanStatisticManager.update(clanMember.clan(), List.of(
                                new StatisticUpdate(StatisticType.KILLS, 1, Operation.SUM),
                                new StatisticUpdate(StatisticType.KILL_STREAK, 1, Operation.SUM))
                        );

                        this.clanManager.updatePoints(clanMember.clan(), this.clanGlobalSettings.pointsPerKill(), Operation.SUM);
                    });
        }

        this.clanMemberCache.getIfPresent(player.getUniqueId())
                .ifPresent(clanMember -> {
                    final Clan clan = clanMember.clan();
                    this.clanStatisticManager.update(clan, List.of(
                            new StatisticUpdate(StatisticType.DEATHS, 1, Operation.SUM),
                            new StatisticUpdate(StatisticType.KILL_STREAK, clan.statistics().killsStreak().value(), Operation.SUB)
                    ));

                    this.clanManager.updatePoints(clanMember.clan(), this.clanGlobalSettings.pointsPerDeath(), Operation.SUM);
                });
    }

}
