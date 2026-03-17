package team.bytephoria.byteclans.api.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import team.bytephoria.byteclans.api.ClanRole;

import java.util.Collection;
import java.util.Optional;

public interface ClanRoleRegistry {

    ClanRole getRole(final @NotNull String roleId);

    ClanRole getDefaultRole();

    ClanRole getOwnerRole();

    Optional<ClanRole> getNextRole(final @NotNull ClanRole current);
    Optional<ClanRole> getPreviousRole(final @NotNull ClanRole current);

    @UnmodifiableView
    Collection<ClanRole> getRoles();

}
