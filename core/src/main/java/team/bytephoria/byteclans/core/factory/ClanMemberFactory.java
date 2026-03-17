package team.bytephoria.byteclans.core.factory;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanChatType;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.ClanRole;
import team.bytephoria.byteclans.api.registry.ClanRoleRegistry;
import team.bytephoria.byteclans.core.clan.DefaultClanMember;
import team.bytephoria.byteclans.core.clan.DefaultClanMemberData;
import team.bytephoria.byteclans.spi.storage.view.ClanMemberView;

public final class ClanMemberFactory {

    public @NotNull DefaultClanMember createWithoutClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanRoleRegistry clanRoleRegistry,
            final boolean isOwner
    ) {
        final ClanRole clanRole = isOwner ? clanRoleRegistry.getOwnerRole() : clanRoleRegistry.getDefaultRole();
        return new DefaultClanMember(
                clanPlayer,
                DefaultClanMemberData.now(),
                clanRole
        );
    }

    public @NotNull DefaultClanMember create(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull Clan clan,
            final @NotNull ClanRoleRegistry clanRoleRegistry,
            final boolean isOwner
    ) {

        return new DefaultClanMember(
                clanPlayer,
                clan,
                DefaultClanMemberData.now(),
                isOwner ? clanRoleRegistry.getOwnerRole() : clanRoleRegistry.getDefaultRole(),
                ClanChatType.PUBLIC
        );
    }

    public @NotNull DefaultClanMember create(
            final @NotNull ClanMemberView memberView,
            final @NotNull Clan clan,
            final @NotNull ClanRoleRegistry clanRoleRegistry,
            final @NotNull ClanPlayer clanPlayer
    ) {

        final ClanRole clanRole = clanRoleRegistry.getRole(memberView.roleId());
        if (clanRole == null) {
            throw new IllegalArgumentException("Invalid clan role ID: " + memberView.roleId());
        }

        return new DefaultClanMember(
                memberView.memberUniqueId(),
                memberView.memberName(),
                clan,
                new DefaultClanMemberData(
                        memberView.joinedAt(),
                        memberView.lastSeenAt()
                ),
                clanRole,
                clanPlayer,
                ClanChatType.PUBLIC
        );
    }

}
