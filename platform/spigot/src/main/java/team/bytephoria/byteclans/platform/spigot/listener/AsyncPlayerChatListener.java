package team.bytephoria.byteclans.platform.spigot.listener;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
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

    public AsyncPlayerChatListener(
            final @NotNull SpigotPlugin spigotPlugin,
            final @NotNull Configuration configuration,
            final @NotNull ComponentSerializerAdapter serializerAdapter,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache
    ) {
        this.spigotPlugin = spigotPlugin;
        this.configuration = configuration;
        this.serializerAdapter = serializerAdapter;
        this.clanMemberCache = clanMemberCache;
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(final @NotNull AsyncPlayerChatEvent playerChatEvent) {
        final Player player = playerChatEvent.getPlayer();
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.chatType() == ClanChatType.PUBLIC) {
            return;
        }

        final String format = this.configuration.clan().chat().clanFormat()
                .replace("{player}", player.getName())
                .replace("{role_display}", clanMember.role().displayName())
                .replace("{message}", playerChatEvent.getMessage());

        final Component message = this.serializerAdapter.deserialize(format);

        clanMember.clan().allMembers().forEach(member -> member.player()
                .ifPresent(clanPlayer -> {
                    if (clanPlayer instanceof BukkitClanPlayer bukkitClanPlayer) {
                        final Player bukkitPlayer = bukkitClanPlayer.bukkitPlayer();
                        final Audience audience = this.spigotPlugin.adventure().player(bukkitPlayer);

                        audience.sendMessage(message);
                    }
                }));

        playerChatEvent.setCancelled(true);
    }
}
