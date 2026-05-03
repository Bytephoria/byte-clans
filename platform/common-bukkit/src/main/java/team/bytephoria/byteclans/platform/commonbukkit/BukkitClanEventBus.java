package team.bytephoria.byteclans.platform.commonbukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.statistic.StatisticType;
import team.bytephoria.byteclans.api.util.Operation;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.bukkitapi.event.*;
import team.bytephoria.byteclans.bukkitapi.event.create.ClanPostCreateAsyncEvent;
import team.bytephoria.byteclans.bukkitapi.event.create.ClanPreCreateAsyncEvent;
import team.bytephoria.byteclans.bukkitapi.event.diplomacy.*;
import team.bytephoria.byteclans.bukkitapi.event.diplomacy.request.ClanAllyRequestAcceptEvent;
import team.bytephoria.byteclans.bukkitapi.event.diplomacy.request.ClanAllyRequestDeclineEvent;
import team.bytephoria.byteclans.bukkitapi.event.diplomacy.request.ClanAllyRequestSendEvent;
import team.bytephoria.byteclans.bukkitapi.event.invite.ClanInviteDeclineEvent;
import team.bytephoria.byteclans.bukkitapi.event.invite.ClanInviteSendEvent;
import team.bytephoria.byteclans.bukkitapi.event.invite.ClanPostInviteAcceptEvent;
import team.bytephoria.byteclans.bukkitapi.event.invite.ClanPreInviteAcceptEvent;
import team.bytephoria.byteclans.bukkitapi.event.member.*;
import team.bytephoria.byteclans.bukkitapi.event.settings.ClanInviteStatusChangeEvent;
import team.bytephoria.byteclans.bukkitapi.event.settings.ClanPvPModeChangeEvent;
import team.bytephoria.byteclans.bukkitapi.event.settings.ClanRenameDisplayEvent;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;

import java.util.UUID;

public final class BukkitClanEventBus implements ClanEventBus {

    @Override
    public boolean callPreCreateClan(final @NotNull ClanPlayer clanPlayer, @NotNull String clanName) {
        final BukkitClanPlayer bukkitClanPlayer = ensureBukkitPlayer(clanPlayer);
        return this.callEvent(new ClanPreCreateAsyncEvent(bukkitClanPlayer, clanName));
    }

    @Override
    public void callPostCreateClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    ) {

        final BukkitClanPlayer bukkitClanPlayer = ensureBukkitPlayer(clanPlayer);
        this.callEvent(new ClanPostCreateAsyncEvent(bukkitClanPlayer, clanMember, clan));
    }

    @Override
    public boolean callDisbandClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    ) {
        final BukkitClanPlayer bukkitClanPlayer = ensureBukkitPlayer(clanPlayer);
        return this.callEvent(new ClanDisbandEvent(bukkitClanPlayer, clanMember, clan));
    }

    @Override
    public boolean callInviteSend(
            final @NotNull ClanMember senderMember,
            final @NotNull Clan targetClan,
            final @NotNull ClanPlayer target
    ) {
        final BukkitClanPlayer targetBukkitClanPlayer = ensureBukkitPlayer(target);
        return this.callEvent(new ClanInviteSendEvent(senderMember, targetClan, targetBukkitClanPlayer));
    }

    @Override
    public boolean callPreInviteAccept(
            final @NotNull ClanPlayer player,
            final @NotNull ClanInvitation invitation,
            final @NotNull Clan clan
    ) {
        final BukkitClanPlayer bukkitClanPlayer = ensureBukkitPlayer(player);
        return this.callEvent(new ClanPreInviteAcceptEvent(bukkitClanPlayer, invitation, clan));
    }

    @Override
    public void callPostInviteAccept(
            final @NotNull ClanPlayer player,
            final @NotNull ClanInvitation invitation,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    ) {
        final BukkitClanPlayer bukkitClanPlayer = ensureBukkitPlayer(player);
        this.callEvent(new ClanPostInviteAcceptEvent(bukkitClanPlayer, invitation, clanMember, clan));
    }

    @Override
    public void callInviteDecline(
            final @NotNull ClanPlayer player,
            final @NotNull ClanInvitation invitation
    ) {
        final BukkitClanPlayer bukkitClanPlayer = ensureBukkitPlayer(player);
        this.callEvent(new ClanInviteDeclineEvent(bukkitClanPlayer, invitation));
    }

    @Override
    public boolean callMemberJoin(
            final @NotNull ClanPlayer player,
            final @NotNull Clan clan
    ) {
        final BukkitClanPlayer bukkitClanPlayer = ensureBukkitPlayer(player);
        return this.callEvent(new ClanMemberJoinEvent(bukkitClanPlayer, clan));
    }

    @Override
    public boolean callMemberDamage(
            final @NotNull ClanMember damager,
            final @NotNull ClanMember damaged
    ) {
        return this.callEvent(new ClanMemberDamageEvent(damager, damaged));
    }

    @Override
    public boolean callMemberLeave(final @NotNull ClanMember member) {
        return this.callEvent(new ClanMemberLeaveEvent(member, member.clan()));
    }

    @Override
    public boolean callMemberKick(
            final @NotNull ClanMember kicker,
            final @NotNull ClanMember kicked
    ) {
        return this.callEvent(new ClanMemberKickEvent(kicker, kicked, kicker.clan()));
    }

    @Override
    public boolean callMemberChangeRole(
            final @NotNull ClanMember changer,
            final @NotNull ClanMember changed,
            final @NotNull ClanRole oldRole,
            final @NotNull ClanRole newRole
    ) {
        return this.callEvent(new ClanMemberRoleChangeEvent(changer, changed, oldRole, newRole, changed.clan()));
    }

    @Override
    public boolean callTransferOwner(
            final @NotNull ClanMember executor,
            final @NotNull ClanMember oldOwner,
            final @NotNull ClanMember newOwner,
            final @NotNull Clan clan
    ) {
        return this.callEvent(new ClanTransferOwnerEvent(executor, oldOwner, newOwner, clan));
    }

    @Override
    public boolean callPvPModeChange(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull ClanPvPMode oldMode,
            final @NotNull ClanPvPMode newMode
    ) {
        return this.callEvent(new ClanPvPModeChangeEvent(clanMember, clan, oldMode, newMode));
    }

    @Override
    public boolean callInviteStatusChange(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull ClanInviteState oldState,
            final @NotNull ClanInviteState newState
    ) {
        return this.callEvent(new ClanInviteStatusChangeEvent(clanMember, clan, oldState, newState));
    }

    @Override
    public boolean callRenameDisplay(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull String oldDisplayName,
            final @NotNull String newDisplayName
    ) {
        return this.callEvent(new ClanRenameDisplayEvent(clanMember, clan, oldDisplayName, newDisplayName));
    }

    @Override
    public boolean callPromoteEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull ClanMember targetMember,
            final @NotNull ClanRole currentRole,
            final @NotNull ClanRole nextRole
    ) {
        return this.callEvent(new ClanPromoteEvent(executorMember, targetMember, currentRole, nextRole));
    }

    @Override
    public boolean callDemoteEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull ClanMember targetMember,
            final @NotNull ClanRole currentRole,
            final @NotNull ClanRole nextRole
    ) {
        return this.callEvent(new ClanDemoteEvent(executorMember, targetMember, currentRole, nextRole));
    }

    @Override
    public boolean callClanAllyAddEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull Clan targetClan
    ) {
        return this.callEvent(new ClanllyAddEvent(executorMember, targetClan));
    }

    @Override
    public boolean callClanAllyRemoveEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return this.callEvent(new ClanAllyRemoveEvent(clanMember, targetClan));
    }

    @Override
    public boolean callClanEnemyAddEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return this.callEvent(new ClanEnemyAddEvent(clanMember, targetClan));
    }

    @Override
    public boolean callClanEnemyRemoveEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return this.callEvent(new ClanEnemyRemoveEvent(clanMember, targetClan));
    }

    @Override
    public void callClanAllyRequestDeclineEvent(final @NotNull ClanMember clanMember, @NotNull UUID targetClanUniqueId) {
        this.callEvent(new ClanAllyRequestDeclineEvent(clanMember, targetClanUniqueId));
    }

    @Override
    public void callClanAllyRequestAcceptEvent(final @NotNull ClanMember clanMember, final @NotNull Clan targetClan) {
        this.callEvent(new ClanAllyRequestAcceptEvent(clanMember, targetClan));
    }

    @Override
    public void callClanTensionAddEvent(
            final @NotNull UUID affectedClanUniqueId,
            final @NotNull String affectedClanName,
            final @Nullable Clan affectedClan,
            final @NotNull Clan targetClan,
            final @NotNull Clan sourceClan
    ) {
        this.callEvent(new ClanTensionAddEvent(affectedClanUniqueId, affectedClanName, affectedClan, targetClan, sourceClan));
    }

    @Override
    public void callClanTensionRemoveEvent(
            final @NotNull UUID affectedClanUniqueId,
            final @NotNull String affectedClanName,
            final @Nullable Clan affectedClan,
            final @NotNull Clan targetClan,
            final @NotNull Clan sourceClan
    ) {
        this.callEvent(new ClanTensionRemoveEvent(affectedClanUniqueId, affectedClanName, affectedClan, targetClan, sourceClan));
    }

    @Override
    public boolean callClanAllyRequestSendEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return this.callEvent(new ClanAllyRequestSendEvent(clanMember, targetClan));
    }

    BukkitClanPlayer ensureBukkitPlayer(final @NotNull ClanPlayer clanPlayer) {
        if (clanPlayer instanceof BukkitClanPlayer bukkitClanPlayer) {
            return bukkitClanPlayer;
        }

        throw new UnsupportedOperationException("ClanPlayer must be an instance of BukkitClanPlayer.");
    }

    @Override
    public boolean callClanPointsChangeEvent(
            final @NotNull Clan clan,
            final int value,
            final int oldValue,
            final int finalValue,
            final @NotNull Operation operation
    ) {
        return this.callEvent(new ClanPointsChangeEvent(clan, value, oldValue, finalValue, operation));
    }

    @Override
    public boolean callClanStatisticUpdateEvent(
            final @NotNull Clan clan,
            final int value,
            final @NotNull Operation operation,
            final @NotNull StatisticType statisticType
    ) {
        return this.callEvent(new ClanStatisticChangeEvent(clan, value, operation, statisticType));
    }

    boolean callEvent(final @NotNull Event event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event instanceof Cancellable cancellable) {
            return !cancellable.isCancelled();
        }

        return true;
    }

}
