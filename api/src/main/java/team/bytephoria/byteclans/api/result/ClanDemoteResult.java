package team.bytephoria.byteclans.api.result;

public enum ClanDemoteResult {

    SUCCESS,
    CANCELLED,
    INSUFFICIENT_ROLE,
    CANNOT_DEMOTE_HIGHER_OR_EQUAL_ROLE,
    CANNOT_DEMOTE_ONESELF,
    ALREADY_MIN_ROLE,
    DISTINCT_CLAN

}
