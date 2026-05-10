package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.api.ClanSettings;

public final class DefaultClanSettings implements ClanSettings {

    private int maxMembers;
    private int maxAllies;
    private int maxEnemies;

    private ClanPvPMode pvpMode;
    private ClanInviteState inviteState;

    public DefaultClanSettings(
            final int maxMembers,
            final int maxAllies,
            final int maxEnemies,
            final @NotNull ClanPvPMode pvpMode,
            final @NotNull ClanInviteState inviteState
    ) {
        this.maxMembers = maxMembers;
        this.maxAllies = maxAllies;
        this.maxEnemies = maxEnemies;
        this.pvpMode = pvpMode;
        this.inviteState = inviteState;
    }

    @Override
    public int maxMembers() {
        return this.maxMembers;
    }

    @Override
    public int maxAllies() {
        return this.maxAllies;
    }

    @Override
    public int maxEnemies() {
        return this.maxEnemies;
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
    public void maxMembers(final int maxMembers) {
        this.maxMembers = maxMembers;
    }

    @Override
    public void maxAllies(final int maxAllies) {
        this.maxAllies = maxAllies;
    }

    @Override
    public void maxEnemies(final int maxEnemies) {
        this.maxEnemies = maxEnemies;
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
