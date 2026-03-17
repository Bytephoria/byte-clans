package team.bytephoria.byteclans.platform.paper.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanChatType;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.infrastructure.adventure.ComponentSerializerAdapter;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.Configuration;

public final class AsyncChatEventListener implements Listener {

    private final Configuration configuration;
    private final ComponentSerializerAdapter serializerAdapter;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    public AsyncChatEventListener(
            final @NotNull Configuration configuration,
            final @NotNull ComponentSerializerAdapter serializerAdapter,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache
    ) {
        this.configuration = configuration;
        this.serializerAdapter = serializerAdapter;
        this.clanMemberCache = clanMemberCache;
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(final @NotNull AsyncChatEvent asyncChatEvent) {
        final Player player = asyncChatEvent.getPlayer();
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.chatType() == ClanChatType.PUBLIC) {
            return;
        }

        final String format = this.configuration.clan().chat().clanFormat()
                .replace("{player}", player.getName())
                .replace("{role_display}", clanMember.role().displayName())
                .replace("{message}", asyncChatEvent.signedMessage().message());

        final Component message = this.serializerAdapter.deserialize(format);
        clanMember.clan().allMembers().forEach(member -> member.player()
                .ifPresent(clanPlayer -> {
                    if (clanPlayer instanceof BukkitClanPlayer bukkitClanPlayer) {
                        final Player bukkitPlayer = bukkitClanPlayer.bukkitPlayer();
                        bukkitPlayer.sendMessage(message);
                    }
                }));

        asyncChatEvent.setCancelled(true);
    }
}
