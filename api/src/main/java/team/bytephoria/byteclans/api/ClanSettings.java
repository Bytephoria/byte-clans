package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;

public interface ClanSettings {

    int maxMembers();

    int maxAllies();

    int maxEnemies();

    ClanPvPMode pvpMode();
    ClanInviteState inviteState();

    void maxMembers(final int maxMembers);
    void maxAllies(final int maxAllies);
    void maxEnemies(final int maxEnemies);

    void pvpMode(final @NotNull ClanPvPMode pvpMode);
    void inviteState(final @NotNull ClanInviteState inviteState);

}
