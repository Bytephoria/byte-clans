package team.bytephoria.byteclans.platform.spigot.loader;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanAction;
import team.bytephoria.byteclans.core.clan.DefaultClanRole;
import team.bytephoria.byteclans.core.registry.DefaultClanRoleRegistry;
import team.bytephoria.byteclans.infrastructure.configuration.roles.Role;
import team.bytephoria.byteclans.infrastructure.configuration.roles.Roles;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            final List<ClanAction> roleActions = role.actions()
                    .stream()
                    .map(actionId -> ClanAction.valueOf(actionId.toUpperCase(Locale.ROOT)))
                    .toList();

            final EnumSet<ClanAction> clanActions = roleActions.isEmpty() ? EnumSet.noneOf(ClanAction.class) : EnumSet.copyOf(roleActions);
            this.clanRoleRegistry.register(
                    new DefaultClanRole(
                            roleId,
                            role.displayName(),
                            role.priority(),
                            clanActions,
                            role.isDefault()
                    )
            );
        }
    }

}
