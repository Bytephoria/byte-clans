package team.bytephoria.byteclans.platform.paper.listener;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.manager.ClanStatisticManager;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;

public final class PlayerDeathListener implements Listener {

    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final ClanStatisticManager clanStatisticManager;

    public PlayerDeathListener(
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanStatisticManager clanStatisticManager
    ) {
        this.clanMemberCache = clanMemberCache;
        this.clanStatisticManager = clanStatisticManager;
    }

    @EventHandler
    public void onPlayerDeath(final @NotNull PlayerDeathEvent deathEvent) {
        final Player player = deathEvent.getPlayer();
        final DamageSource damageSource = deathEvent.getDamageSource();

        if (damageSource.getCausingEntity() instanceof Player killer && player != killer) {
            this.clanMemberCache.getIfPresent(player.getUniqueId())
                    .ifPresent(clanMember -> this.clanStatisticManager.addKillsAndKs(clanMember.clan(), 1, 1));

        }

        this.clanMemberCache.getIfPresent(player.getUniqueId())
                .ifPresent(clanMember -> this.clanStatisticManager.addDeathAndResetKs(clanMember.clan(), 1));

    }

}
