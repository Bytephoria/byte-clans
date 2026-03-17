package team.bytephoria.byteclans.api.access;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.manager.*;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;
import team.bytephoria.byteclans.api.validator.ClanNameValidator;
import team.bytephoria.byteclans.api.registry.ClanRoleRegistry;

import java.util.Optional;
import java.util.UUID;

public interface ByteClans {

    static @NotNull ByteClans getAPI() {
        return ByteClansProvider.getInstance();
    }

    ClanNameValidator nameValidator();

    ClanDisplayNameValidator displayNameValidator();

    ClanRoleRegistry roleRegistry();

    ClanInviteManager inviteManager();

    ClanManager clanManager();

    ClanMemberManager memberManager();

    ClanSettingsManager settingsManager();

    ClanStatisticManager statisticManager();

    Clan getClanOrNull(final @NotNull UUID clanUniqueId);

    Clan getClanByNameOrNull(final String clanName);

    ClanMember getMemberOrNull(final @NotNull ClanPlayer clanPlayer);

    ClanMember getMemberOrNull(final @NotNull UUID memberUniqueId);

    default Optional<Clan> getClanByName(final @NotNull String clanName) {
        final Clan clan = this.getClanByNameOrNull(clanName);
        return Optional.ofNullable(clan);
    }

    default Optional<Clan> getClan(final @NotNull UUID clanUniqueId) {
        final Clan clan = this.getClanOrNull(clanUniqueId);
        return Optional.ofNullable(clan);
    }

    default Optional<ClanMember> getMember(final @NotNull UUID playerUniqueId) {
        final ClanMember clanMember = this.getMemberOrNull(playerUniqueId);
        return Optional.ofNullable(clanMember);
    }

    default Optional<ClanMember> getMember(final @NotNull ClanPlayer clanPlayer) {
        final ClanMember clanMember = this.getMemberOrNull(clanPlayer);
        return Optional.ofNullable(clanMember);
    }

}
