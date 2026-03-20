package team.bytephoria.byteclans.platform.spigot.command;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanManager;
import team.bytephoria.byteclans.api.manager.ClanMemberManager;
import team.bytephoria.byteclans.api.manager.ClanSettingsManager;
import team.bytephoria.byteclans.api.registry.ClanRoleRegistry;
import team.bytephoria.byteclans.api.result.*;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;
import team.bytephoria.byteclans.core.util.ClanNameUUID;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.platform.commonbukkit.concurrent.AsyncExecutor;
import team.bytephoria.byteclans.platform.spigot.SpigotPlugin;
import team.bytephoria.byteclans.platform.spigot.message.Messenger;

import java.util.*;

public final class ClanAdminCommands {

    private final SpigotPlugin spigotPlugin;

    private final IdentityCachedMap<ClanMember> memberCache;
    private final IdentityCachedMap<Clan> clanCache;

    private final ClanManager clanManager;
    private final ClanMemberManager clanMemberManager;
    private final ClanSettingsManager clanSettingsManager;
    private final ClanRoleRegistry clanRoleRegistry;
    private final Messenger messenger;

    public ClanAdminCommands(
            final @NotNull SpigotPlugin spigotPlugin,
            final @NotNull IdentityCachedMap<ClanMember> memberCache,
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull ClanManager clanManager,
            final @NotNull ClanMemberManager clanMemberManager,
            final @NotNull ClanSettingsManager clanSettingsManager,
            final @NotNull ClanRoleRegistry clanRoleRegistry,
            final @NotNull Messenger messenger
    ) {
        this.spigotPlugin = spigotPlugin;
        this.memberCache = memberCache;
        this.clanCache = clanCache;
        this.clanManager = clanManager;
        this.clanMemberManager = clanMemberManager;
        this.clanSettingsManager = clanSettingsManager;
        this.clanRoleRegistry = clanRoleRegistry;
        this.messenger = messenger;
    }

    @Command("clan admin disband <clanName>")
    @Permission("byteclans.command.admin.disband")
    public void disbandClan(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName
    ) {
        AsyncExecutor.runAsync(() -> {
            final ResponseContext<Clan, ClanDisbandResult> response = this.clanManager.admin().disbandClanByName(clanName);
            final ClanDisbandResult result = response.result();
            final String path = "clan.admin.disband." + this.resolveEnumName(result);

            this.spigotPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS -> this.messenger.sendPathMessage(player, path, Map.of("clan", clanName));
                    case NOT_FOUND -> this.messenger.sendPathMessage(player, path, Map.of("clan", clanName));
                }
            });
        });
    }

    @Command("clan admin transfer <clanName> <target>")
    @Permission("byteclans.command.admin.transfer")
    public void transferClan(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName,
            final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") Player targetPlayer
    ) {
        AsyncExecutor.runAsync(() -> {
            final Response<ClanTransferResult> response = this.clanMemberManager.admin().transfer(
                    clanName,
                    targetPlayer.getUniqueId(),
                    targetPlayer.getName()
            );

            final ClanTransferResult result = response.result();
            final String path = "clan.admin.transfer." + this.resolveEnumName(result);

            this.spigotPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS, CLAN_NOT_FOUND, NOT_IN_CLAN, TARGET_IS_ALREADY_OWNER -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName, "target", targetPlayer.getName())
                    );
                }
            });
        });
    }

    @Command("clan admin kick <target>")
    @Permission("byteclans.command.admin.kick")
    public void kickMember(
            final @NotNull Player player,
            final @NotNull @Argument("target") Player targetPlayer
    ) {
        AsyncExecutor.runAsync(() -> {
            final Response<ClanKickResult> response = this.clanMemberManager.admin().kick(targetPlayer.getUniqueId());
            final ClanKickResult result = response.result();
            final String path = "clan.admin.kick." + this.resolveEnumName(result);

            this.spigotPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS,
                         TARGET_NOT_IN_CLAN,
                         CANNOT_KICK_OWNER -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("target", targetPlayer.getName())
                    );
                }
            });
        });
    }

    @Command("clan admin role <target> <role_id>")
    @Permission("byteclans.command.admin.role")
    public void changeRole(
            final @NotNull Player player,
            final @NotNull @Argument("target") Player targetPlayer,
            final @NotNull @Argument(value = "role_id", suggestions = "registered-roles") String roleId
    ) {
        final ClanRole clanRole = this.clanRoleRegistry.getRole(roleId);
        if (clanRole == null) {
            this.messenger.sendPathMessage(player, "clan.admin.role.not-found");
            return;
        }

        AsyncExecutor.runAsync(() -> {
            final Response<ClanRoleChangeResult> response = this.clanMemberManager.admin().changeRole(targetPlayer.getUniqueId(), clanRole);
            final ClanRoleChangeResult result = response.result();
            final String path = "clan.admin.role." + this.resolveEnumName(result);

            this.spigotPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("target", targetPlayer.getName(), "role", clanRole.displayName())
                    );
                    case MEMBER_NOT_FOUND,
                         ALREADY_SET,
                         CANNOT_ASSIGN_OWNER_ROLE -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("target", targetPlayer.getName())
                    );
                }
            });
        });
    }

    @Command("clan admin display <clanName> <newDisplayName>")
    @Permission("byteclans.command.admin.display")
    public void changeDisplay(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName,
            final @NotNull @Argument("newDisplayName") String newDisplayName
    ) {
        AsyncExecutor.runAsync(() -> {
            final UUID clanUniqueId = ClanNameUUID.from(clanName);
            final Response<ClanRenameDisplayResult> response = this.clanSettingsManager.admin().renameDisplay(clanUniqueId, newDisplayName);
            final ClanRenameDisplayResult result = response.result();
            final String path = "clan.admin.display." + this.resolveEnumName(result);

            this.spigotPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName, "display", newDisplayName)
                    );
                    case NOT_FOUND,
                         INVALID_CONTENT,
                         INVALID_CHARACTERS -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName)
                    );
                }
            });
        });
    }

    @Command("clan admin pvp-mode <clanName> <pvp-mode>")
    @Permission("byteclans.command.admin.pvp-mode")
    public void changePvpMode(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName,
            final @NotNull @Argument(value = "pvp-mode", suggestions = "pvp-modes") String pvpMode
    ) {
        final ClanPvPMode clanPvPMode;
        try {
            clanPvPMode = ClanPvPMode.valueOf(pvpMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.messenger.sendPathMessage(player, "clan.admin.pvp-mode.invalid");
            return;
        }

        AsyncExecutor.runAsync(() -> {
            final UUID clanUniqueId = ClanNameUUID.from(clanName);
            final Response<ClanPvPModeChangeResult> response = this.clanSettingsManager.admin().changePvPMode(clanUniqueId, clanPvPMode);
            final ClanPvPModeChangeResult result = response.result();
            final String path = "clan.admin.pvp-mode." + this.resolveEnumName(result);

            this.spigotPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName, "mode", clanPvPMode.name())
                    );
                    case NOT_FOUND,
                         ALREADY_IN_MODE -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName)
                    );
                }
            });
        });
    }

    @Command("clan admin invite-state <clanName> <invite-state>")
    @Permission("byteclans.command.admin.invite-state")
    public void setInviteState(
            final @NotNull Player player,
            final @NotNull @Argument(value = "clanName", suggestions = "online-clans") String clanName,
            final @NotNull @Argument(value = "invite-state", suggestions = "invite-states") String inviteState
    ) {
        final ClanInviteState clanInviteState;
        try {
            clanInviteState = ClanInviteState.valueOf(inviteState.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.messenger.sendPathMessage(player, "clan.admin.invite-state.invalid");
            return;
        }

        AsyncExecutor.runAsync(() -> {
            final UUID clanUniqueId = ClanNameUUID.from(clanName);
            final Response<ClanStatusChangeResult> response = this.clanSettingsManager.admin().changeInviteStatus(clanUniqueId, clanInviteState);
            final ClanStatusChangeResult result = response.result();
            final String path = "clan.admin.invite-state." + this.resolveEnumName(result);

            this.spigotPlugin.runMainThread(() -> {
                switch (result) {
                    case SUCCESS -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName, "state", clanInviteState.name())
                    );
                    case NOT_FOUND,
                         ALREADY_SET -> this.messenger.sendPathMessage(
                            player,
                            path,
                            Map.of("clan", clanName)
                    );
                }
            });
        });
    }

    private @NotNull String resolveEnumName(final @NotNull Enum<?> enumValue) {
        return enumValue.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    @Suggestions("registered-roles")
    public @NotNull @Unmodifiable List<Suggestion> registeredRoles() {
        return this.clanRoleRegistry.getRoles()
                .stream()
                .map(ClanRole::id)
                .map(Suggestion::suggestion)
                .toList();
    }

    @Suggestions("online-clans")
    public @NotNull @Unmodifiable List<Suggestion> onlineClans() {
        return this.clanCache.valuesCopy()
                .stream()
                .map(clan -> clan.data().name())
                .map(Suggestion::suggestion)
                .toList();
    }

    @Suggestions("pvp-modes")
    public @NotNull @Unmodifiable List<Suggestion> pvpModes() {
        return Arrays.stream(ClanPvPMode.values())
                .map(ClanPvPMode::name)
                .map(Suggestion::suggestion)
                .toList();
    }

    @Suggestions("invite-states")
    public @NotNull @Unmodifiable List<Suggestion> inviteStates() {
        return Arrays.stream(ClanInviteState.values())
                .map(ClanInviteState::name)
                .map(Suggestion::suggestion)
                .toList();
    }

}
