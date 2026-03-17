package team.bytephoria.byteclans.api;

public interface ClanGlobalSettings {

    int defaultMaxMembers();

    ClanPvPMode defaultPvPMode();

    ClanInviteState defaultInviteState();

    int minimumNameChars();
    int maximumNameChars();

}
