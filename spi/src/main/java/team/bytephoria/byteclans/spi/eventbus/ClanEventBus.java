package team.bytephoria.byteclans.spi.eventbus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanInvitation;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.api.ClanRole;
import team.bytephoria.byteclans.api.util.IntValue;

import java.util.UUID;

public interface ClanEventBus {

    default boolean callPreCreateClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull String clanName
    ) {
        return true;
    }

    void callPostCreateClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    );

    default boolean callDisbandClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    ){
        return true;
    }

    default boolean callInviteSend(
            final @NotNull ClanMember senderMember,
            final @NotNull Clan targetClan,
            final @NotNull ClanPlayer target
    ) {
        return true;
    }

    default boolean callPreInviteAccept(
            final @NotNull ClanPlayer player,
            final @NotNull ClanInvitation invitation,
            final @NotNull Clan clan
    ) {
        return true;
    }

    default void callPostInviteAccept(
            final @NotNull ClanPlayer player,
            final @NotNull ClanInvitation invitation,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    ) {
    }

    void callInviteDecline(
            final @NotNull ClanPlayer player,
            final @NotNull ClanInvitation invitation
    );

    default boolean callMemberJoin(
            final @NotNull ClanPlayer player,
            final @NotNull Clan clan
    ) {
        return true;
    }

    boolean callMemberDamage(
            final @NotNull ClanMember damager,
            final @NotNull ClanMember damaged
    );

    default boolean callMemberLeave(
            final @NotNull ClanMember member
    ) {
        return true;
    }

    boolean callMemberKick(
            final @NotNull ClanMember kicker,
            final @NotNull ClanMember kicked
    );

    default boolean callMemberChangeRole(
            final @NotNull ClanMember changer,
            final @NotNull ClanMember changed,
            final @NotNull ClanRole oldRole,
            final @NotNull ClanRole newRole
    ) {
        return true;
    }

    default boolean callTransferOwner(
            final @NotNull ClanMember executor,
            final @NotNull ClanMember oldOwner,
            final @NotNull ClanMember newOwner,
            final @NotNull Clan clan
    ) {
        return true;
    }

    default boolean callPvPModeChange(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull ClanPvPMode oldMode,
            final @NotNull ClanPvPMode newMode
    ) {
        return true;
    }

    default boolean callInviteStatusChange(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull ClanInviteState oldState,
            final @NotNull ClanInviteState newState
    ) {
        return true;
    }

    default boolean callRenameDisplay(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull String oldDisplayName,
            final @NotNull String newDisplayName
    ) {
        return true;
    }

    default boolean callPromoteEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull ClanMember targetMember,
            final @NotNull ClanRole currentRole,
            final @NotNull ClanRole nextRole
    ) {
        return true;
    }

    default boolean callDemoteEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull ClanMember targetMember,
            final @NotNull ClanRole currentRole,
            final @NotNull ClanRole nextRole

    ) {
        return true;
    }

    default boolean callClanAllyAddEvent(
            final @NotNull ClanMember executorMember,
            final @NotNull Clan targetClan
    ) {
        return true;
    }

    default boolean callClanAllyRemoveEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return true;
    }

    default boolean callClanEnemyAddEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return true;
    }

    default boolean callClanEnemyRemoveEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return true;
    }

    default boolean callClanAllyRequestSendEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    ) {
        return true;
    }

    void callClanAllyRequestDeclineEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull UUID targetClanUniqueId
    );

    void callClanAllyRequestAcceptEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan targetClan
    );

    void callClanTensionAddEvent(
            final @NotNull UUID affectedClanUniqueId,
            final @NotNull String affectedClanName,
            final @Nullable Clan affectedClan,
            final @NotNull Clan targetClan,
            final @NotNull Clan sourceClan
    );

    void callClanTensionRemoveEvent(
            final @NotNull UUID affectedClanUniqueId,
            final @NotNull String affectedClanName,
            final @Nullable Clan affectedClan,
            final @NotNull Clan targetClan,
            final @NotNull Clan sourceClan
    );

    default boolean callClanPointsChangeEvent(
            final @NotNull Clan clan,
            final int value,
            final int oldValue,
            final int finalValue,
            final @NotNull IntValue.Operation operation
    ) {
        return true;
    }

}
