package team.bytephoria.byteclans.platform.spigot.listener;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanChatType;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.infrastructure.adventure.ComponentSerializerAdapter;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.Configuration;
import team.bytephoria.byteclans.platform.spigot.SpigotPlugin;

public final class AsyncPlayerChatListener implements Listener {

    private final SpigotPlugin spigotPlugin;
    private final Configuration configuration;
    private final ComponentSerializerAdapter serializerAdapter;
    private final IdentityCachedMap<ClanMember> clanMemberCache;
    private final IdentityCachedMap<Clan> clanCache;

    public AsyncPlayerChatListener(
            final @NotNull SpigotPlugin spigotPlugin,
            final @NotNull Configuration configuration,
            final @NotNull ComponentSerializerAdapter serializerAdapter,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull IdentityCachedMap<Clan> clanCache
    ) {
        this.spigotPlugin = spigotPlugin;
        this.configuration = configuration;
        this.serializerAdapter = serializerAdapter;
        this.clanMemberCache = clanMemberCache;
        this.clanCache = clanCache;
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(final @NotNull AsyncPlayerChatEvent playerChatEvent) {
        final Player player = playerChatEvent.getPlayer();
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.chatType() == ClanChatType.PUBLIC) {
            return;
        }

        final String rawMessage = playerChatEvent.getMessage();
        if (clanMember.chatType() == ClanChatType.CLAN) {
            final String format = this.configuration.clan().chat().clanFormat()
                    .replace("{player}", player.getName())
                    .replace("{role_display}", clanMember.role().displayName())
                    .replace("{message}", rawMessage);

            this.broadcastToClan(clanMember.clan(), this.serializerAdapter.deserialize(format));
        } else if (clanMember.chatType() == ClanChatType.ALLY) {
            final String format = this.configuration.clan().chat().allyFormat()
                    .replace("{player}", player.getName())
                    .replace("{clan}", clanMember.clan().data().name())
                    .replace("{clan_display}", clanMember.clan().data().displayName())
                    .replace("{role_display}", clanMember.role().displayName())
                    .replace("{message}", rawMessage);

            final Component message = this.serializerAdapter.deserialize(format);
            this.broadcastToClan(clanMember.clan(), message);
            clanMember.clan().relations().allies().forEach(ally -> {
                final Clan allyClan = this.clanCache.get(ally.clanUniqueId());
                if (allyClan == null) {
                    return;
                }

                this.broadcastToClan(allyClan, message);
            });
        }

        playerChatEvent.setCancelled(true);
    }

    private void broadcastToClan(
            final @NotNull Clan clan,
            final @NotNull Component message
    ) {
        clan.allMembers().forEach(member ->
                member.player().ifPresent(clanPlayer -> {
                    if (clanPlayer instanceof BukkitClanPlayer bukkitClanPlayer) {
                        final Player bukkitPlayer = bukkitClanPlayer.bukkitPlayer();
                        final Audience audience = this.spigotPlugin.adventure().player(bukkitPlayer);

                        audience.sendMessage(message);
                    }
                })
        );
    }
}