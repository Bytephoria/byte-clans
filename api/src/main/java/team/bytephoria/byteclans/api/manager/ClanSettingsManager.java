package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.api.result.ClanPvPModeChangeResult;
import team.bytephoria.byteclans.api.result.ClanRenameDisplayResult;
import team.bytephoria.byteclans.api.result.ClanStatusChangeResult;
import team.bytephoria.byteclans.api.util.response.Response;

public interface ClanSettingsManager {

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
            final @NotNull String newDisplayName
    );

}
