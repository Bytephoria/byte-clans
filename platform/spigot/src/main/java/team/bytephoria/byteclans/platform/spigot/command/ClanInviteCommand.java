package team.bytephoria.byteclans.platform.spigot.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanInvitation;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.manager.ClanInviteManager;
import team.bytephoria.byteclans.api.result.ClanInviteAcceptResult;
import team.bytephoria.byteclans.api.result.ClanInviteDeclineResult;
import team.bytephoria.byteclans.api.result.ClanInviteSendResult;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.platform.spigot.message.Messenger;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ClanInviteCommand {

    private final ClanInviteManager clanInviteManager;
    private final Messenger messenger;

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    public ClanInviteCommand(
            final @NotNull ClanInviteManager clanInviteManager,
            final @NotNull Messenger messenger,
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache
    ) {
        this.clanInviteManager = clanInviteManager;
        this.messenger = messenger;
        this.clanCache = clanCache;
        this.clanMemberCache = clanMemberCache;
    }

    @Command("clan invite send <target>")
    public void sendInvite(
            final @NotNull Player player,
            final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") Player target
    ) {
        final ClanPlayer playerClanPlayer = BukkitClanPlayer.wrap(player);
        final ClanPlayer targetClanPlayer = BukkitClanPlayer.wrap(target);

        final ClanMember clanMember = this.clanMemberCache.get(playerClanPlayer);
        if (clanMember == null) {
            this.messenger.sendPathMessage(player, "invite.send.not-in-clan");
            return;
        }

        final ResponseContext<ClanInvitation, ClanInviteSendResult> context = this.clanInviteManager.send(
                clanMember,
                targetClanPlayer
        );

        final ClanInviteSendResult result = context.result();
        final String path = "invite.send." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                this.messenger.sendPathMessage(
                        player,
                        path,
                        Map.of("target", target.getName())
                );
                this.messenger.sendPathMessage(
                        target,
                        "invite.send.received",
                        Map.of(
                                "player", player.getName(),
                                "clan", clanMember.clan().data().displayName()
                        )
                );
            }
            case INSUFFICIENT_ROLE,
                 ALREADY_IN_CLAN,
                 ALREADY_INVITED,
                 CLAN_FULL,
                 NOT_IN_CLAN,
                 INVITES_CLOSED,
                 CANNOT_INVITED_ONESELF -> this.messenger.sendPathMessage(
                    player,
                    path,
                    Map.of("target", target.getName())
            );
        }
    }

    @Command("clan invite accept")
    public void acceptInvite(final @NotNull Player player) {
        final ClanPlayer clanPlayer = BukkitClanPlayer.wrap(player);
        final ResponseContext<ClanInvitation, ClanInviteAcceptResult> context = this.clanInviteManager.accept(clanPlayer);

        final ClanInviteAcceptResult result = context.result();
        final String path = "invite.accept." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                final ClanInvitation invitation = context.value();
                final Clan targetClan = this.clanCache.get(invitation.clanUniqueId());

                this.messenger.sendPathMessage(
                        player,
                        path,
                        Map.of("clan", targetClan.data().name())
                );

                targetClan.allMembers().forEach(member -> {
                    if (member.uniqueId().equals(player.getUniqueId())) {
                        return;
                    }

                    final Player notifiedBukkitPlayer = Bukkit.getPlayer(member.uniqueId());
                    if (notifiedBukkitPlayer == null) {
                        return;
                    }

                    this.messenger.sendPathMessage(
                            notifiedBukkitPlayer,
                            "invite.accept.broadcast",
                            Map.of("player", player.getName())
                    );
                });
            }
            case NO_PENDING_INVITE,
                 ALREADY_IN_CLAN,
                 CLAN_FULL -> this.messenger.sendPathMessage(player, path);
        }
    }

    @Command("clan invite decline")
    public void declineInvite(final @NotNull Player player) {
        final ClanPlayer clanPlayer = BukkitClanPlayer.wrap(player);
        final ResponseContext<ClanInvitation, ClanInviteDeclineResult> context = this.clanInviteManager.decline(clanPlayer);
        final ClanInviteDeclineResult result = context.result();
        final String path = "invite.decline." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                final ClanInvitation invitation = context.value();
                final Clan targetClan = this.clanCache.get(invitation.clanUniqueId());

                this.messenger.sendPathMessage(player, path, Map.of("clan", targetClan.data().name()));
                final Player senderBukkitPlayer = Bukkit.getPlayer(invitation.senderUniqueId());
                if (senderBukkitPlayer == null) {
                    return;
                }

                this.messenger.sendPathMessage(
                        senderBukkitPlayer,
                        "invite.decline.notified",
                        Map.of("player", player.getName())
                );

            }
            case NO_PENDING_INVITE -> this.messenger.sendPathMessage(player, path);
        }
    }

    @Suggestions("onlinePlayers")
    public @NonNull @Unmodifiable List<Suggestion> onlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .map(Suggestion::suggestion)
                .toList();
    }

    private @NonNull String resolveEnumName(final @NotNull Enum<?> enumClass) {
        return enumClass.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

}