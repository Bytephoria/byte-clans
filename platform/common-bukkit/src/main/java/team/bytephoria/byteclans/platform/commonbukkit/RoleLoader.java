package team.bytephoria.byteclans.platform.commonbukkit;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanPermissions;
import team.bytephoria.byteclans.api.ClanPermission;
import team.bytephoria.byteclans.core.clan.DefaultClanRole;
import team.bytephoria.byteclans.core.registry.DefaultClanRoleRegistry;
import team.bytephoria.byteclans.infrastructure.configuration.roles.Role;
import team.bytephoria.byteclans.infrastructure.configuration.roles.Roles;

import java.util.*;

public final class RoleLoader {

    private final Roles roles;
    private final DefaultClanRoleRegistry clanRoleRegistry;

    public RoleLoader(
            final @NotNull Roles roles,
            final @NotNull DefaultClanRoleRegistry clanRoleRegistry
    ) {
        this.roles = roles;
        this.clanRoleRegistry = clanRoleRegistry;
    }

    public void loadAll() {
        for (final Map.Entry<String, Role> roleEntry : this.roles.roles().entrySet()) {
            final String roleId = roleEntry.getKey();
            final Role role = roleEntry.getValue();
            final List<ClanPermissions> permissions = role.actions()
                    .stream()
                    .map(actionId -> ClanPermissions.valueOf(actionId.toUpperCase(Locale.ROOT)))
                    .toList();

            final Set<ClanPermission> clanPermissions = new HashSet<>(permissions);

            this.clanRoleRegistry.register(
                    new DefaultClanRole(
                            roleId,
                            role.displayName(),
                            role.priority(),
                            clanPermissions,
                            role.isDefault()
                    )
            );
        }
    }

}
