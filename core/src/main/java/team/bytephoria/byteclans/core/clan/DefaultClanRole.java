package team.bytephoria.byteclans.core.clan;

import team.bytephoria.byteclans.api.ClanAction;
import team.bytephoria.byteclans.api.ClanRole;

import java.util.EnumSet;

public record DefaultClanRole(
        String id,
        String displayName,
        int priority,
        EnumSet<ClanAction> actions,
        boolean isDefault
) implements ClanRole {

}
