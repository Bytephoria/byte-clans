package team.bytephoria.byteclans.api;

import java.time.Duration;

public interface ClanGlobalSettings {

    int defaultMaxMembers();

    ClanPvPMode defaultPvPMode();

    ClanInviteState defaultInviteState();

    Duration displayNameChangeCooldown();

    default Duration clanCreationTimeout() {
        return Duration.ZERO;
    }

    int minimumNameChars();

    int maximumNameChars();

    int maximumAllies();

    int maximumEnemies();

    int minimumPoints();

    int maximumPoints();

    int pointsPerKill();

    int pointsPerDeath();

}
