package team.bytephoria.byteclans.api.result;

public enum ClanKickResult {

    SUCCESS,
    CANCELLED,
    NOT_IN_CLAN,
    TARGET_NOT_IN_CLAN,
    INSUFFICIENT_ROLE,
    CANNOT_KICK_OWNER,
    CANNOT_KICK_ONESELF,
    CANNOT_KICK_HIGHER_ROLE,
    DISTINCT_CLAN

}
