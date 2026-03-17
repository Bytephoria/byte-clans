package team.bytephoria.byteclans.api.result;

public enum ClanPromoteResult {

    SUCCESS,
    CANCELLED,
    INSUFFICIENT_ROLE,
    ALREADY_MAX_ROLE,
    CANNOT_PROMOTE_ONESELF,
    CANNOT_PROMOTE_HIGHER_OR_EQUAL_ROLE,
    CANNOT_PROMOTE_TO_HIGHER_ROLE,
    DISTINCT_CLAN

}
