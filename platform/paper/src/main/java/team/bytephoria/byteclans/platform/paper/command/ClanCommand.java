package team.bytephoria.byteclans.platform.paper.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanManager;
import team.bytephoria.byteclans.api.manager.ClanMemberManager;
import team.bytephoria.byteclans.api.manager.ClanSettingsManager;
import team.bytephoria.byteclans.api.result.*;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.bukkitapi.FeaturePermissions;
import team.bytephoria.byteclans.core.util.ClanNameUUID;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.platform.commonbukkit.concurrent.AsyncExecutor;
import team.bytephoria.byteclans.platform.paper.PaperPlugin;
import team.bytephoria.byteclans.platform.paper.message.Messenger;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ClanCommand {

    private final PaperPlugin paperPlugin;
    private final Messenger messenger;
    private final ClanManager clanManager;
    private final ClanSettingsManager clanSettingsManager;
    private final ClanMemberManager clanMemberManager;
    private final ClanGlobalSettings clanGlobalSettings;

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    public ClanCommand(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull Messenger messenger,
            final @NotNull ClanManager clanManager,
            final @NotNull ClanSettingsManager clanSettingsManager,
            final @NotNull ClanMemberManager clanMemberManager,
            final @NotNull ClanGlobalSettings clanGlobalSettings,
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache
    ) {
        this.paperPlugin = paperPlugin;
        this.messenger = messenger;
        this.clanManager = clanManager;
        this.clanSettingsManager = clanSettingsManager;
        this.clanMemberManager = clanMemberManager;
        this.clanGlobalSettings = clanGlobalSettings;

        this.clanCache = clanCache;
        this.clanMemberCache = clanMemberCache;
    }

    @Command("clan create <name>")
    @Permission(FeaturePermissions.CREATE_CLAN)
    public void createClan(
            final @NotNull Player player,
            final @NotNull @Argument("name") String clanName
    ) {

        AsyncExecutor.runAsync(() -> {
            final BukkitClanPlayer bukkitClanPlayer = BukkitClanPlayer.wrap(player);
            final ResponseContext<Clan, ClanCreateResult> context = this.clanManager.createClan(bukkitClanPlayer, clanName);
            final ClanCreateResult result = context.result();
            final String path = "clan.create." + this.resolveEnumName(result);

            this.paperPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS, NAME_TAKEN -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName)
                    );
                    case NAME_TOO_SHORT -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("min", Integer.toString(this.clanGlobalSettings.minimumNameChars()))
                    );
                    case NAME_TOO_LONG -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("max", Integer.toString(this.clanGlobalSettings.maximumNameChars()))
                    );
                    case ALREADY_IN_CLAN, CANCELLED, NAME_INVALID_CHARACTERS -> this.messenger.sendPathMessage(player, path);
                }
            });
        }).exceptionally(throwable -> {
            this.paperPlugin.getSLF4JLogger().info("An error has occurred while creating a clan: {}", throwable.getMessage(), throwable);
            return null;
        });

    }

    @Command("clan disband")
    public void disbandClan(final @NotNull Player player) {
        final ClanPlayer clanPlayer = BukkitClanPlayer.wrap(player);
        final ResponseContext<Clan, ClanDisbandResult> context = this.clanManager.disbandClan(clanPlayer);
        final ClanDisbandResult result = context.result();
        final String path = "clan.disband." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> this.messenger.sendPathMessage(
                    player,
                    path,
                    Map.of("clan", context.value().data().name())
            );

            case NOT_IN_CLAN, INSUFFICIENT_ROLE, CANCELLED -> this.messenger.sendPathMessage(player, path);
        }
    }

    @Command("clan transfer <target>")
    public void transferClan(
            final @NotNull Player player,
            final @NotNull @Argument("target") Player target
    ) {
        final ClanMember playerMember = this.clanMemberCache.get(player.getUniqueId());
        if (playerMember == null) {
            this.messenger.sendPathMessage(player, "clan.transfer.not-in-clan");
            return;
        }

        final ClanMember targetMember = this.clanMemberCache.get(target.getUniqueId());
        if (targetMember == null) {
            this.messenger.sendPathMessage(player, "clan.transfer.target-not-in-clan", Map.of("target", target.getName()));
            return;
        }

        final Response<ClanTransferResult> context = this.clanMemberManager.transferOwner(playerMember, targetMember);
        final ClanTransferResult result = context.result();
        final String path = "clan.transfer." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                this.messenger.sendPathMessage(
                        player,
                        path,
                        Map.of("target", target.getName())
                );
                this.messenger.sendPathMessage(
                        target,
                        "clan.transfer.received",
                        Map.of("clan", playerMember.clan().data().displayName())
                );

                playerMember.clan().allMembers().forEach(member -> {
                    if (member.uniqueId().equals(player.getUniqueId())) {
                        return;
                    }

                    if (member.uniqueId().equals(target.getUniqueId())) {
                        return;
                    }

                    final Player memberBukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
                    if (memberBukkitPlayer == null) {
                        return;
                    }

                    this.messenger.sendPathMessage(
                            memberBukkitPlayer,
                            "clan.transfer.broadcast",
                            Map.of("target", target.getName())
                    );
                });
            }
            case NOT_IN_CLAN, INSUFFICIENT_ROLE, DISTINCT_CLAN,
                 TARGET_NOT_IN_CLAN, TARGET_IS_ALREADY_OWNER, CANCELLED ->
                    this.messenger.sendPathMessage(player, path, Map.of("target", target.getName()));
        }
    }

    @Command("clan kick <target>")
    public void kickMember(
            final @NotNull Player player,
            final @NotNull @Argument("target") Player target
    ) {
        final ClanMember playerMember = this.clanMemberCache.get(player.getUniqueId());
        if (playerMember == null) {
            this.messenger.sendPathMessage(player, "clan.kick.not-in-clan");
            return;
        }

        final ClanMember targetMember = this.clanMemberCache.get(target.getUniqueId());
        if (targetMember == null) {
            this.messenger.sendPathMessage(player, "clan.kick.target-not-in-clan", Map.of("target", target.getName()));
            return;
        }

        final Response<ClanKickResult> context = this.clanMemberManager.kick(playerMember, targetMember);
        final ClanKickResult result = context.result();
        final String path = "clan.kick." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                this.messenger.sendPathMessage(
                        player,
                        path,
                        Map.of("target", target.getName())
                );

                this.messenger.sendPathMessage(
                        target,
                        "clan.kick.kicked",
                        Map.of("clan", playerMember.clan().data().displayName())
                );

                playerMember.clan().allMembers().forEach(member -> {
                    if (member.uniqueId().equals(player.getUniqueId())) {
                        return;
                    }

                    if (member.uniqueId().equals(target.getUniqueId())) {
                        return;
                    }

                    final Player memberBukkitPlayer = Bukkit.getPlayer(member.uniqueId());
                    if (memberBukkitPlayer == null) {
                        return;
                    }

                    this.messenger.sendPathMessage(
                            memberBukkitPlayer,
                            "clan.kick.broadcast",
                            Map.of("target", target.getName())
                    );
                });
            }
            case NOT_IN_CLAN, INSUFFICIENT_ROLE, DISTINCT_CLAN,
                 CANNOT_KICK_OWNER, CANNOT_KICK_ONESELF, CANNOT_KICK_HIGHER_ROLE,
                 TARGET_NOT_IN_CLAN, CANCELLED ->
                    this.messenger.sendPathMessage(player, path, Map.of("target", target.getName()));
        }
    }

    @Command("clan leave")
    public void leaveClan(final @NotNull Player player) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null) {
            this.messenger.sendPathMessage(player, "clan.leave.not-in-clan");
            return;
        }

        final String clanName = clanMember.clan().data().name();
        final Response<ClanLeaveResult> context = this.clanMemberManager.leave(clanMember);
        final ClanLeaveResult result = context.result();
        final String path = "clan.leave." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                this.messenger.sendPathMessage(
                        player,
                        path,
                        Map.of("clan", clanName)
                );

                clanMember.clan().allMembers().forEach(member -> {
                    if (member.uniqueId().equals(player.getUniqueId())) {
                        return;
                    }

                    final Player memberBukkitPlayer =  Bukkit.getPlayer(member.uniqueId());
                    if (memberBukkitPlayer == null) {
                        return;
                    }

                    this.messenger.sendPathMessage(
                            memberBukkitPlayer,
                            "clan.leave.broadcast",
                            Map.of("player", player.getName())
                    );
                });
            }
            case NOT_IN_CLAN, OWNER_CANNOT_LEAVE, CANCELLED ->
                    this.messenger.sendPathMessage(player, path);
        }
    }

    @Command("clan promote <target>")
    public void promoteMember(
            final @NotNull Player player,
            final @Argument("target") Player target
    ) {
        final ClanMember playerMember = this.clanMemberCache.get(player.getUniqueId());
        if (playerMember == null) {
            this.messenger.sendPathMessage(player, "clan.promote.not-in-clan");
            return;
        }

        final ClanMember targetMember = this.clanMemberCache.get(target.getUniqueId());
        if (targetMember == null) {
            this.messenger.sendPathMessage(player, "clan.promote.target-not-in-clan", Map.of("target", target.getName()));
            return;
        }

        final Response<ClanPromoteResult> context = this.clanMemberManager.promote(playerMember, targetMember);
        final ClanPromoteResult result = context.result();
        final String path = "clan.promote." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                this.messenger.sendPathMessage(
                        player,
                        path,
                        Map.of(
                                "target", target.getName(),
                                "role", targetMember.role().displayName()
                        )
                );

                this.messenger.sendPathMessage(
                        target,
                        "clan.promote.received",
                        Map.of("role", targetMember.role().displayName())
                );

                playerMember.clan().allMembers().forEach(member -> {
                    if (member.uniqueId().equals(player.getUniqueId())) {
                        return;
                    }

                    if (member.uniqueId().equals(target.getUniqueId())) {
                        return;
                    }

                    final Player memberBukkitPlayer = Bukkit.getPlayer(member.uniqueId());
                    if (memberBukkitPlayer == null) {
                        return;
                    }

                    this.messenger.sendPathMessage(
                            memberBukkitPlayer,
                            "clan.promote.broadcast",
                            Map.of(
                                    "target", target.getName(),
                                    "role", targetMember.role().displayName()
                            )
                    );
                });
            }
            case INSUFFICIENT_ROLE,
                 DISTINCT_CLAN,
                 ALREADY_MAX_ROLE,
                 CANNOT_PROMOTE_ONESELF,
                 CANNOT_PROMOTE_TO_HIGHER_ROLE,
                 CANNOT_PROMOTE_HIGHER_OR_EQUAL_ROLE,
                 CANCELLED -> this.messenger.sendPathMessage(player, path, Map.of("target", target.getName()));
        }
    }

    @Command("clan display <display>")
    @Permission(FeaturePermissions.DISPLAY_MODIFY)
    public void changeDisplay(
            final @NotNull Player player,
            final @Argument("display") String display
    ) {

        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null) {
            this.messenger.sendPathMessage(player, "clan.display.not-in-clan");
            return;
        }

        final Duration duration = this.paperPlugin.configuration().clan().display().cooldown().toDuration();
        final Response<ClanRenameDisplayResult> response = this.clanSettingsManager.renameDisplay(clanMember, display, duration);
        final ClanRenameDisplayResult result = response.result();
        final String path = "clan.display." + this.resolveEnumName(result);

        this.messenger.sendPathMessage(player, path, Map.of("current_display", display, "new_display", display));
    }

    @Command("clan demote <target>")
    public void demoteMember(
            final @NotNull Player player,
            final @NotNull @Argument("target") Player target
    ) {
        final ClanMember playerMember = this.clanMemberCache.get(player.getUniqueId());
        if (playerMember == null) {
            this.messenger.sendPathMessage(player, "clan.demote.not-in-clan");
            return;
        }

        final ClanMember targetMember = this.clanMemberCache.get(target.getUniqueId());
        if (targetMember == null) {
            this.messenger.sendPathMessage(player, "clan.demote.target-not-in-clan", Map.of("target", target.getName()));
            return;
        }

        final Response<ClanDemoteResult> context = this.clanMemberManager.demote(playerMember, targetMember);
        final ClanDemoteResult result = context.result();
        final String path = "clan.demote." + this.resolveEnumName(result);

        switch (result) {
            case SUCCESS -> {
                this.messenger.sendPathMessage(
                        player,
                        path,
                        Map.of(
                                "target", target.getName(),
                                "role", targetMember.role().displayName()
                        )
                );

                this.messenger.sendPathMessage(
                        target,
                        "clan.demote.received",
                        Map.of("role", targetMember.role().displayName())
                );

                playerMember.clan().allMembers().forEach(member -> {
                    if (member.uniqueId().equals(player.getUniqueId())) {
                        return;
                    }

                    if (member.uniqueId().equals(target.getUniqueId())) {
                        return;
                    }

                    final Player memberBukkitPlayer = Bukkit.getPlayer(member.uniqueId());
                    if (memberBukkitPlayer == null) {
                        return;
                    }

                    this.messenger.sendPathMessage(
                            memberBukkitPlayer,
                            "clan.demote.broadcast",
                            Map.of(
                                    "target", target.getName(),
                                    "role", targetMember.role().displayName()
                            )
                    );
                });
            }
            case INSUFFICIENT_ROLE,
                 DISTINCT_CLAN,
                 ALREADY_MIN_ROLE,
                 CANNOT_DEMOTE_ONESELF,
                 CANNOT_DEMOTE_HIGHER_OR_EQUAL_ROLE,
                 CANCELLED -> this.messenger.sendPathMessage(player, path, Map.of("target", target.getName()));
        }
    }

    @Command("clan chat <chat_type>")
    public void changeChatMode(
            final @NotNull Player player,
            final @NotNull @Argument(value = "chat_type", suggestions = "chat_types") String chatType
    ) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null) {
            this.messenger.sendPathMessage(player, "clan.chat.clan.not-in-clan");
            return;
        }

        final ClanChatType clanChatType;

        try {
            clanChatType = ClanChatType.valueOf(chatType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            this.messenger.sendPathMessage(player, "clan.chat.invalid-mode");
            return;
        }

        final Response<ClanChangeChatModeResult> context = this.clanMemberManager.changeChatMode(clanMember, clanChatType);
        final ClanChangeChatModeResult result = context.result();

        switch (result) {
            case SUCCESS -> {
                final String path = switch (clanChatType) {
                    case CLAN, ALLY -> "clan.chat.clan.switched";
                    case PUBLIC -> "clan.chat.clan.switched-back";
                };

                this.messenger.sendPathMessage(player, path);
            }
            case NOT_IN_CLAN -> this.messenger.sendPathMessage(player, "clan.chat.clan.not-in-clan");
            case ALREADY_IN_MODE -> this.messenger.sendPathMessage(player, "clan.chat.already-in-mode");
            case INSUFFICIENT_ONLINE_MEMBERS -> this.messenger.sendPathMessage(player, "clan.chat.insufficient-online-members");
        }
    }

    @Command("clan info")
    public void clanInfo(final @NotNull Player player) {
        final ClanMember clanMember = this.clanMemberCache.get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            this.messenger.sendPathMessage(player, "clan.info.not-in-clan");
            return;
        }

        final Clan clan = clanMember.clan();
        final Map<String, String> placeholders = this.clanPlaceholders(clan);
        placeholders.put("role", clanMember.role().displayName());

        this.messenger.sendListMessage(player, placeholders, "clan", "info", "success-own");
    }

    @Command("clan info <clan_name>")
    public void clanInfoOther(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clan_name", suggestions = "online_clans") String clanName
    ) {

        final UUID clanUniqueId = ClanNameUUID.from(clanName);
        final Clan clan = this.clanCache.get(clanUniqueId);
        if (clan == null) {
            this.messenger.sendPathMessage(player, "clan.info.not-found");
            return;
        }

        final Map<String, String> placeholders = this.clanPlaceholders(clan);
        this.messenger.sendListMessage(player, placeholders, "clan", "info", "success-other");
    }

    private @NotNull Map<String, String> clanPlaceholders(final @NotNull Clan clan) {
        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("name", clan.data().name());
        placeholders.put("display", clan.data().displayName());
        placeholders.put("created_at", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(clan.data().createdAt())
        );

        placeholders.put("points", clan.points().toString());

        placeholders.put("owner_name", clan.ownerData().name());
        placeholders.put("owner_unique_id", clan.ownerData().uniqueId().toString());

        placeholders.put("invite_state", clan.settings().inviteState().toString());
        placeholders.put("pvp_mode", clan.settings().pvpMode().toString());

        placeholders.put("allies", String.join(", ", clan.relations().allies().stream()
                .map(ClanRelation::clanName)
                .toList()));

        placeholders.put("enemies", String.join(", ", clan.relations().enemies().stream()
                .map(ClanRelation::clanName)
                .toList()));

        placeholders.put("kills", clan.statistics().kills().toString());
        placeholders.put("deaths", clan.statistics().deaths().toString());
        placeholders.put("kill_streak", clan.statistics().killsStreak().toString());

        placeholders.put("members_size", Integer.toString(clan.allMembers().size()));
        placeholders.put("max_members", Integer.toString(clan.settings().maxMembers()));

        placeholders.put("members", String.join(", ", clan.allMembers().stream().map(ClanMember::name).toList()));
        placeholders.put("online_members", String.join(", ", clan.allMembers().stream()
                .filter(clanMember1 -> Bukkit.getPlayer(clanMember1.uniqueId()) != null)
                .map(ClanMember::name)
                .toList())
        );

        return placeholders;
    }

    @Suggestions("chat_types")
    public @NotNull @Unmodifiable List<Suggestion> chatTypesSuggestions() {
        return Arrays.stream(ClanChatType.values())
                .map(chatType -> chatType.name().toLowerCase(Locale.ROOT))
                .map(Suggestion::suggestion)
                .toList();
    }

    @Suggestions("online_clans")
    public @NotNull @Unmodifiable List<Suggestion> onlineClansSuggestions() {
        return this.clanCache.valuesCopy().stream()
                .map(clan -> clan.data().name())
                .map(Suggestion::suggestion)
                .toList();
    }

    private @NonNull String resolveEnumName(final @NotNull Enum<?> enumClass) {
        return enumClass.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

}
