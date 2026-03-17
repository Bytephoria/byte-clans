package team.bytephoria.byteclans.api;

import java.util.EnumSet;

public interface ClanRole {

    String id();

    String displayName();

    int priority();

    EnumSet<ClanAction> actions();

    boolean isDefault();

}
