package team.bytephoria.byteclans.core.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.manager.ClanSettingsManager;
import team.bytephoria.byteclans.api.util.response.Response;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;
import team.bytephoria.byteclans.api.result.ClanDisplayNameValidationResult;
import team.bytephoria.byteclans.api.result.ClanPvPModeChangeResult;
import team.bytephoria.byteclans.api.result.ClanRenameDisplayResult;
import team.bytephoria.byteclans.api.result.ClanStatusChangeResult;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;

public final class DefaultClanSettingsManager implements ClanSettingsManager {

    private final ClanStorage clanStorage;
    private final ClanEventBus clanEventBus;
    private final ClanDisplayNameValidator clanDisplayNameValidator;

    public DefaultClanSettingsManager(
            final @NotNull ClanStorage clanStorage,
            final @NotNull ClanEventBus clanEventBus,
            final @NotNull ClanDisplayNameValidator clanDisplayNameValidator
    ) {
        this.clanStorage = clanStorage;
        this.clanEventBus = clanEventBus;
        this.clanDisplayNameValidator = clanDisplayNameValidator;
    }

    @Override
    public Response<ClanPvPModeChangeResult> changePvPMode(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanPvPMode newPvPMode
    ) {

        final Clan clan = clanMember.clan();
        if (clan == null) {
            return Response.failure(ClanPvPModeChangeResult.NOT_IN_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.CHANGE_PVP_MODE)) {
            return Response.failure(ClanPvPModeChangeResult.INSUFFICIENT_ROLE);
        }

        final ClanPvPMode currentPvPMode = clan.settings().pvpMode();
        if (currentPvPMode == newPvPMode) {
            return Response.failure(ClanPvPModeChangeResult.ALREADY_IN_MODE);
        }

        if (!this.clanEventBus.callPvPModeChange(clanMember, clan, currentPvPMode, newPvPMode)) {
            return Response.failure(ClanPvPModeChangeResult.CANCELLED);
        }

        clan.settings().pvpMode(newPvPMode);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.PVP_MODE);
        return Response.success(ClanPvPModeChangeResult.SUCCESS);
    }

    @Override
    public Response<ClanStatusChangeResult> changeInviteStatus(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanInviteState newInviteState
    ) {

        final Clan clan = clanMember.clan();
        if (clan == null) {
            return Response.failure(ClanStatusChangeResult.NOT_IN_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.CHANGE_INVITE_STATE)) {
            return Response.failure(ClanStatusChangeResult.INSUFFICIENT_ROLE);
        }

        final ClanInviteState currentInviteState = clan.settings().inviteState();
        if (currentInviteState == newInviteState) {
            return Response.failure(ClanStatusChangeResult.ALREADY_SET);
        }

        if (!this.clanEventBus.callInviteStatusChange(clanMember, clan, currentInviteState, newInviteState)) {
            return Response.failure(ClanStatusChangeResult.CANCELLED);
        }

        clan.settings().inviteState(newInviteState);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.INVITE_STATE);
        return Response.success(ClanStatusChangeResult.SUCCESS);
    }

    @Override
    public Response<ClanRenameDisplayResult> renameDisplay(
            final @NotNull ClanMember clanMember,
            final @NotNull String newDisplayName
    ) {
        final Clan clan = clanMember.clan();
        if (clan == null) {
            return Response.failure(ClanRenameDisplayResult.NOT_IN_CLAN);
        }

        if (!clanMember.hasPermission(ClanAction.RENAME_DISPLAY)) {
            return Response.failure(ClanRenameDisplayResult.INSUFFICIENT_ROLE);
        }

        final ClanDisplayNameValidationResult result = this.clanDisplayNameValidator.validate(
                clan.data().name(),
                newDisplayName
        );

        if (result != ClanDisplayNameValidationResult.VALID) {
            return Response.failure(ClanRenameDisplayResult.valueOf(result.name()));
        }

        final String currentDisplayName = clan.data().displayName();
        if (!this.clanEventBus.callRenameDisplay(clanMember, clan, currentDisplayName, newDisplayName)) {
            return Response.failure(ClanRenameDisplayResult.CANCELLED);
        }

        clan.data().displayName(newDisplayName);
        this.clanStorage.async().update(ClanEntry.from(clan), ClanField.DISPLAY_NAME);
        return Response.success(ClanRenameDisplayResult.SUCCESS);
    }
}
