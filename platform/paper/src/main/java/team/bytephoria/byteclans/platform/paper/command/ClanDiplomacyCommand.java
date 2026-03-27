package team.bytephoria.byteclans.platform.paper.command;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanRequestAlly;
import team.bytephoria.byteclans.api.manager.ClanRelationAllyRequestManager;
import team.bytephoria.byteclans.api.manager.ClanRelationManager;
import team.bytephoria.byteclans.api.result.*;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.core.util.ClanNameUUID;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.platform.paper.message.Messenger;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ClanDiplomacyCommand {

    private final Messenger messenger;
    private final ClanRelationManager clanRelationManager;
    private final ClanRelationAllyRequestManager clanRelationAllyRequestManager;

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    public ClanDiplomacyCommand(
            final @NotNull Messenger messenger,
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanRelationManager clanRelationManager,
            final @NotNull ClanRelationAllyRequestManager clanRelationAllyRequestManager
    ) {
        this.messenger = messenger;
        this.clanRelationManager = clanRelationManager;
        this.clanRelationAllyRequestManager = clanRelationAllyRequestManager;
        this.clanCache = clanCache;
        this.clanMemberCache = clanMemberCache;
    }

    @Command("clan ally send <clanName>")
    public void sendAllyRequest(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName
    ) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.not-in-clan");
            return;
        }

        final Clan targetClan = this.clanCache.get(ClanNameUUID.from(clanName));
        if (targetClan == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.target-not-found");
            return;
        }

        final ResponseContext<ClanRequestAlly, ClanAllyRequestSendResult> response = this.clanRelationAllyRequestManager.send(clanMember, targetClan);
        final String path = "clan.diplomacy.ally.send." + this.resolveEnumName(response.result());
        this.messenger.sendPathMessage(player, path, Map.of("clan", targetClan.data().name()));

        if (response.success()) {
            final Clan ownClan = clanMember.clan();

            this.notifyClanMembers(
                    clanMember,
                    ownClan,
                    "clan.diplomacy.ally.send.broadcast",
                    Map.of(
                            "player", player.getName(),
                            "clan", targetClan.data().name()
                    )
            );

            this.notifyClanMembers(
                    targetClan,
                    "clan.diplomacy.ally.send.received",
                    Map.of(
                            "player", player.getName(),
                            "clan", ownClan.data().name()
                    )
            );
        }
    }

    @Command("clan ally accept")
    public void acceptAllyRequest(final @NotNull Player player) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.not-in-clan");
            return;
        }

        final ResponseContext<ClanRequestAlly, ClanAllyRequestAcceptResult> response =
                this.clanRelationAllyRequestManager.accept(clanMember);

        final String path = "clan.diplomacy.ally.accept." + this.resolveEnumName(response.result());
        this.messenger.sendPathMessage(player, path);

        if (response.success()) {
            final Clan senderClan = this.clanCache.get(response.value().clanSenderUniqueId());
            final Clan receiverClan = clanMember.clan();

            this.notifyClanMembers(
                    clanMember,
                    senderClan,
                    "clan.diplomacy.ally.accept.notified",
                    Map.of("clan", receiverClan.data().name())
            );

            this.notifyClanMembers(
                    receiverClan,
                    "clan.diplomacy.ally.accept.broadcast",
                    Map.of("clan", senderClan.data().name())
            );
        }
    }

    @Command("clan ally decline")
    public void declineAllyRequest(final @NotNull Player player) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.not-in-clan");
            return;
        }

        final ResponseContext<ClanRequestAlly, ClanAllyRequestDeclineResult> response =
                this.clanRelationAllyRequestManager.decline(clanMember);

        final String path = "clan.diplomacy.ally.decline." + this.resolveEnumName(response.result());
        this.messenger.sendPathMessage(player, path);

        if (response.success()) {
            final Clan senderClan = this.clanCache.get(response.value().clanSenderUniqueId());

            this.notifyClanMembers(
                    clanMember,
                    senderClan,
                    "clan.diplomacy.ally.decline.notified",
                    Map.of("player", player.getName())
            );
        }
    }

    @Command("clan ally remove <clanName>")
    public void removeAlly(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName
    ) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.not-in-clan");
            return;
        }

        final Clan targetClan = this.clanCache.get(ClanNameUUID.from(clanName));
        if (targetClan == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.target-not-found");
            return;
        }

        final Response<ClanAllyRemoveResult> response = this.clanRelationManager.removeAllyClan(clanMember, targetClan);
        final String path = "clan.diplomacy.ally.remove." + this.resolveEnumName(response.result());
        this.messenger.sendPathMessage(player, path, Map.of("clan", targetClan.data().name()));

        if (response.success()) {
            final Clan ownClan = clanMember.clan();

            this.notifyClanMembers(
                    clanMember,
                    ownClan,
                    "clan.diplomacy.ally.remove.broadcast",
                    Map.of("clan", targetClan.data().name())
            );

            this.notifyClanMembers(
                    targetClan,
                    "clan.diplomacy.ally.remove.notified",
                    Map.of("clan", ownClan.data().name())
            );
        }
    }

    @Command("clan enemy add <clanName>")
    public void addEnemy(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName
    ) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.not-in-clan");
            return;
        }

        final Clan targetClan = this.clanCache.get(ClanNameUUID.from(clanName));
        if (targetClan == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.target-not-found");
            return;
        }

        final Response<ClanEnemyAddResult> response =
                this.clanRelationManager.addEnemyClan(clanMember, targetClan);

        final String path = "clan.diplomacy.enemy.add." + this.resolveEnumName(response.result());
        this.messenger.sendPathMessage(player, path, Map.of("clan", targetClan.data().name()));

        if (response.success()) {
            final Clan ownClan = clanMember.clan();

            this.notifyClanMembers(
                    clanMember,
                    ownClan,
                    "clan.diplomacy.enemy.add.broadcast",
                    Map.of("clan", targetClan.data().name())
            );

            this.notifyClanMembers(
                    targetClan,
                    "clan.diplomacy.enemy.add.notified",
                    Map.of("clan", ownClan.data().name())
            );
        }
    }

    @Command("clan enemy remove <clanName>")
    public void removeEnemy(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName
    ) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.not-in-clan");
            return;
        }

        final Clan targetClan = this.clanCache.get(ClanNameUUID.from(clanName));
        if (targetClan == null) {
            this.messenger.sendPathMessage(player, "clan.diplomacy.target-not-found");
            return;
        }

        final Response<ClanEnemyRemoveResult> response =
                this.clanRelationManager.removeEnemyClan(clanMember, targetClan);

        final String path = "clan.diplomacy.enemy.remove." + this.resolveEnumName(response.result());
        this.messenger.sendPathMessage(player, path, Map.of("clan", targetClan.data().name()));

        if (response.success()) {
            final Clan ownClan = clanMember.clan();

            this.notifyClanMembers(
                    clanMember,
                    ownClan,
                    "clan.diplomacy.enemy.remove.broadcast",
                    Map.of("clan", targetClan.data().name())
            );

            this.notifyClanMembers(
                    targetClan,
                    "clan.diplomacy.enemy.remove.notified",
                    Map.of("clan", ownClan.data().name())
            );
        }
    }

    private void notifyClanMembers(
            final @NotNull Clan clan,
            final @NotNull String path,
            final @NotNull Map<String, String> placeholders
    ) {
        this.notifyClanMembers(null, clan, path, placeholders);
    }

    private void notifyClanMembers(
            final @Nullable ClanMember except,
            final @NotNull Clan clan,
            final @NotNull String path,
            final @NotNull Map<String, String> placeholders
    ) {
        final Set<ClanMember> members = new HashSet<>(clan.allMembers());
        if (except != null) {
            members.remove(except);
        }

        for (final ClanMember member : members) {
            member.player().ifPresent(clanPlayer -> {
                if (clanPlayer instanceof BukkitClanPlayer bukkitClanPlayer) {
                    this.messenger.sendPathMessage(bukkitClanPlayer.bukkitPlayer(), path, placeholders);
                }
            });
        }
    }

    private @NotNull String resolveEnumName(final @NotNull Enum<?> enumClass) {
        return enumClass.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }
}