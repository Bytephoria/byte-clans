package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.api.ClanSettings;

public final class DefaultClanSettings implements ClanSettings {

    private int maxMembers;

    private ClanPvPMode pvpMode;
    private ClanInviteState inviteState;

    public DefaultClanSettings(
            final int maxMembers,
            final @NotNull ClanPvPMode pvpMode,
            final @NotNull ClanInviteState inviteState
    ) {
        this.maxMembers = maxMembers;
        this.pvpMode = pvpMode;
        this.inviteState = inviteState;
    }

    @Override
    public int maxMembers() {
        return this.maxMembers;
    }

    @Override
    public ClanPvPMode pvpMode() {
        return this.pvpMode;
    }

    @Override
    public ClanInviteState inviteState() {
        return this.inviteState;
    }

    @Override
    public void maxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    @Override
    public void pvpMode(final @NotNull ClanPvPMode pvpMode) {
        this.pvpMode = pvpMode;
    }

    @Override
    public void inviteState(final @NotNull ClanInviteState inviteState) {
        this.inviteState = inviteState;
    }

}
