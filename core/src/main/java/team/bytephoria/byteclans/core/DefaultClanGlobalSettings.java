package team.bytephoria.byteclans.core;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;

import java.time.Duration;

public final class DefaultClanGlobalSettings implements ClanGlobalSettings {

    private final int defaultMaxMembers;
    private final ClanPvPMode defaultClanPvPMode;
    private final ClanInviteState defaultClanInviteState;
    private final Duration displayNameChangeCooldown;

    private final int minimumPoints;
    private final int maximumPoints;

    private final int maximumAllies;
    private final int maximumEnemies;

    private final int pointsPerKill;
    private final int pointsPerDeath;

    private final int minimumChars;
    private final int maximumChars;

    public DefaultClanGlobalSettings(
            final int defaultMaxMembers,
            final @NotNull ClanPvPMode defaultClanPvPMode,
            final @NotNull ClanInviteState defaultClanInviteState,
            final @NotNull Duration displayNameChangeCooldown,
            final int minimumChars,
            final int maximumChars,
            final int maximumAllies,
            final int maximumEnemies,
            final int minimumPoints,
            final int maximumPoints,
            final int pointsPerKill,
            final int pointsPerDeath
    ) {
        this.defaultMaxMembers = defaultMaxMembers;

        this.defaultClanPvPMode = defaultClanPvPMode;
        this.defaultClanInviteState = defaultClanInviteState;
        this.displayNameChangeCooldown = displayNameChangeCooldown;

        this.minimumChars = minimumChars;
        this.maximumChars = maximumChars;

        this.maximumAllies = maximumAllies;
        this.maximumEnemies = maximumEnemies;

        this.minimumPoints = minimumPoints;
        this.maximumPoints = maximumPoints;

        this.pointsPerKill = pointsPerKill;
        this.pointsPerDeath = pointsPerDeath;

    }

    @Override
    public int defaultMaxMembers() {
        return this.defaultMaxMembers;
    }

    @Override
    public ClanPvPMode defaultPvPMode() {
        return this.defaultClanPvPMode;
    }

    @Override
    public ClanInviteState defaultInviteState() {
        return this.defaultClanInviteState;
    }

    @Override
    public Duration displayNameChangeCooldown() {
        return this.displayNameChangeCooldown;
    }

    @Override
    public int minimumNameChars() {
        return this.minimumChars;
    }

    @Override
    public int maximumNameChars() {
        return this.maximumChars;
    }

    @Override
    public int maximumAllies() {
        return this.maximumAllies;
    }

    @Override
    public int maximumEnemies() {
        return this.maximumEnemies;
    }

    @Override
    public int minimumPoints() {
        return this.minimumPoints;
    }

    @Override
    public int maximumPoints() {
        return this.maximumPoints;
    }

    @Override
    public int pointsPerKill() {
        return this.pointsPerKill;
    }

    @Override
    public int pointsPerDeath() {
        return this.pointsPerDeath;
    }
}
