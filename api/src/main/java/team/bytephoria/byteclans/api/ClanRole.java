package team.bytephoria.byteclans.api;

import java.util.Set;

public interface ClanRole {

    String id();

    String displayName();

    int priority();

    Set<ClanPermission> permissions();

    boolean isDefault();

}
