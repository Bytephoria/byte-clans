package team.bytephoria.byteclans.api;

public interface ClanStatistics {

    int kills();
    int deaths();
    int killsStreak();
    double kdr();

    void kills(final int kills);
    void deaths(final int deaths);
    void killsStreak(final int killsStreak);

}
