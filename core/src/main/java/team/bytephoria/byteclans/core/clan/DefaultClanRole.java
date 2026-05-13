package team.bytephoria.byteclans.core.clan;

import team.bytephoria.byteclans.api.ClanPermission;
import team.bytephoria.byteclans.api.ClanRole;

import java.util.Set;

public record DefaultClanRole(
        String id,
        String displayName,
        int priority,
        Set<ClanPermission> permissions,
        boolean isDefault
) implements ClanRole {

}
