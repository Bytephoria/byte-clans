package team.bytephoria.byteclans.core.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import team.bytephoria.byteclans.api.ClanAction;
import team.bytephoria.byteclans.api.ClanRole;
import team.bytephoria.byteclans.api.registry.ClanRoleRegistry;

import java.util.*;

public final class DefaultClanRoleRegistry implements ClanRoleRegistry {

    private final Map<String, ClanRole> registry = new HashMap<>();

    private ClanRole defaultRole;
    private ClanRole ownerRole;

    public void register(final @NotNull ClanRole clanRole) {
        this.registry.put(clanRole.id(), clanRole);

        if (clanRole.isDefault()) {
            this.defaultRole = clanRole;
        }

        if (clanRole.actions().contains(ClanAction.ADMINISTRATOR)) {
            this.ownerRole = clanRole;
        }

    }

    @Override
    public ClanRole getRole(final @NotNull String roleId) {
        return this.registry.get(roleId);
    }

    @Override
    public ClanRole getDefaultRole() {
        if (this.defaultRole == null) {
            throw new IllegalStateException("No default clan role has been registered.");
        }

        return this.defaultRole;
    }

    @Override
    public ClanRole getOwnerRole() {
        if (this.ownerRole == null) {
            throw new IllegalStateException("No owner clan role has been registered.");
        }

        return this.ownerRole;
    }

    @Override
    public @NotNull Optional<ClanRole> getNextRole(final @NotNull ClanRole current) {
        return this.registry.values().stream()
                .filter(clanRole -> clanRole.priority() > current.priority())
                .min(Comparator.comparingInt(ClanRole::priority));
    }

    @Override
    public @NotNull Optional<ClanRole> getPreviousRole(final @NotNull ClanRole current) {
        return this.registry.values().stream()
                .filter(clanRole -> clanRole.priority() < current.priority())
                .max(Comparator.comparingInt(ClanRole::priority));
    }

    @Override
    public @UnmodifiableView @NotNull Collection<ClanRole> getRoles() {
        return Collections.unmodifiableCollection(this.registry.values());
    }
}
