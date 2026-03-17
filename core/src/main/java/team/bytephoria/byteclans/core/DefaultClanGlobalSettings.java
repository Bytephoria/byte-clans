package team.bytephoria.byteclans.core;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;

public final class DefaultClanGlobalSettings implements ClanGlobalSettings {

    private final int defaultMaxMembers;
    private final ClanPvPMode defaultClanPvPMode;
    private final ClanInviteState defaultClanInviteState;

    private final int minimumChars;
    private final int maximumChars;

    public DefaultClanGlobalSettings(
            final int defaultMaxMembers,
            final @NotNull ClanPvPMode defaultClanPvPMode,
            final  @NotNull ClanInviteState defaultClanInviteState,
            final int minimumChars,
            final int maximumChars
    ) {
        this.defaultMaxMembers = defaultMaxMembers;

        this.defaultClanPvPMode = defaultClanPvPMode;
        this.defaultClanInviteState = defaultClanInviteState;

        this.minimumChars = minimumChars;
        this.maximumChars = maximumChars;
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
    public int minimumNameChars() {
        return this.minimumChars;
    }

    @Override
    public int maximumNameChars() {
        return this.maximumChars;
    }
}
