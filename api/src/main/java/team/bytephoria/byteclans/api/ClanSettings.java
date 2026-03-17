package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;

public interface ClanSettings {

    int maxMembers();

    ClanPvPMode pvpMode();
    ClanInviteState inviteState();

    void maxMembers(final int maxMembers);
    void pvpMode(final @NotNull ClanPvPMode pvpMode);
    void inviteState(final @NotNull ClanInviteState inviteState);

}
