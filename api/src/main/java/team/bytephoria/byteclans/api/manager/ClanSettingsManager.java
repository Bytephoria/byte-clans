package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.api.result.ClanPvPModeChangeResult;
import team.bytephoria.byteclans.api.result.ClanRenameDisplayResult;
import team.bytephoria.byteclans.api.result.ClanStatusChangeResult;
import team.bytephoria.byteclans.api.util.response.Response;

import java.time.Duration;
import java.util.UUID;

public interface ClanSettingsManager {

    interface Admin {

        Response<ClanPvPModeChangeResult> changePvPMode(
                final @NotNull UUID clanUniqueId,
                final @NotNull ClanPvPMode clanPvPMode
        );

        Response<ClanStatusChangeResult> changeInviteStatus(
                final @NotNull UUID clanUniqueId,
                final @NotNull ClanInviteState clanInviteState
        );

        Response<ClanRenameDisplayResult> renameDisplay(
                final @NotNull UUID clanUniqueId,
                final @NotNull String newDisplayName
        );

    }

    Admin admin();

    Response<ClanPvPModeChangeResult> changePvPMode(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanPvPMode newPvPMode
    );

    Response<ClanStatusChangeResult> changeInviteStatus(
            final @NotNull ClanMember clanMember,
            final @NotNull ClanInviteState newInviteState
    );

    Response<ClanRenameDisplayResult> renameDisplay(
            final @NotNull ClanMember clanMember,
            final @NotNull String newDisplayName,
            final @Nullable Duration cooldown
    );

    Response<ClanRenameDisplayResult> renameDisplay(
            final @NotNull ClanMember clanMember,
            final @NotNull String newDisplayName
    );

}
