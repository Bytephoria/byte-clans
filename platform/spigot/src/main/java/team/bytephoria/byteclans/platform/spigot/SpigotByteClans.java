package team.bytephoria.byteclans.platform.spigot;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.manager.*;
import team.bytephoria.byteclans.api.registry.ClanRoleRegistry;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;
import team.bytephoria.byteclans.api.validator.ClanNameValidator;
import team.bytephoria.byteclans.core.util.ClanNameUUID;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;

import java.util.UUID;

public final class SpigotByteClans implements ByteClans {

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    private final ClanRoleRegistry clanRoleRegistry;
    private final ClanInviteManager clanInviteManager;
    private final ClanManager clanManager;

    private final ClanMemberManager clanMemberManager;
    private final ClanSettingsManager clanSettingsManager;
    private final ClanStatisticManager clanStatisticManager;

    private final ClanNameValidator clanNameValidator;
    private final ClanDisplayNameValidator clanDisplayNameValidator;

    public SpigotByteClans(
            final @NotNull IdentityCachedMap<Clan> clanCache,
            final @NotNull IdentityCachedMap<ClanMember> clanMemberCache,
            final @NotNull ClanRoleRegistry clanRoleRegistry,
            final @NotNull ClanInviteManager clanInviteManager,
            final @NotNull ClanManager clanManager,
            final @NotNull ClanMemberManager clanMemberManager,
            final @NotNull ClanSettingsManager clanSettingsManager,
            final @NotNull ClanStatisticManager clanStatisticManager,
            final @NotNull ClanNameValidator clanNameValidator,
            final @NotNull ClanDisplayNameValidator clanDisplayNameValidator
    ) {
        this.clanCache = clanCache;
        this.clanMemberCache = clanMemberCache;

        this.clanRoleRegistry = clanRoleRegistry;
        this.clanInviteManager = clanInviteManager;
        this.clanManager = clanManager;
        this.clanMemberManager = clanMemberManager;
        this.clanSettingsManager = clanSettingsManager;
        this.clanStatisticManager = clanStatisticManager;
        this.clanNameValidator = clanNameValidator;
        this.clanDisplayNameValidator = clanDisplayNameValidator;
    }

    @Override
    public ClanNameValidator nameValidator() {
        return this.clanNameValidator;
    }

    @Override
    public ClanDisplayNameValidator displayNameValidator() {
        return this.clanDisplayNameValidator;
    }

    @Override
    public ClanRoleRegistry roleRegistry() {
        return this.clanRoleRegistry;
    }

    @Override
    public ClanInviteManager inviteManager() {
        return this.clanInviteManager;
    }

    @Override
    public ClanManager clanManager() {
        return this.clanManager;
    }

    @Override
    public ClanMemberManager memberManager() {
        return this.clanMemberManager;
    }

    @Override
    public ClanSettingsManager settingsManager() {
        return this.clanSettingsManager;
    }

    @Override
    public ClanStatisticManager statisticManager() {
        return this.clanStatisticManager;
    }

    @Override
    public Clan getClanOrNull(final @NotNull UUID clanUniqueId) {
        return this.clanCache.get(clanUniqueId);
    }

    @Override
    public Clan getClanByNameOrNull(final @NotNull String clanName) {
        final UUID clanUniqueId = ClanNameUUID.from(clanName);
        return this.getClanOrNull(clanUniqueId);
    }

    @Override
    public ClanMember getMemberOrNull(final @NotNull ClanPlayer clanPlayer) {
        return this.clanMemberCache.get(clanPlayer);
    }

    @Override
    public ClanMember getMemberOrNull(final @NotNull UUID memberUniqueId) {
        return this.clanMemberCache.get(memberUniqueId);
    }
}
