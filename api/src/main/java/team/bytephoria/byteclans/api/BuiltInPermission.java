package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;

public enum BuiltInPermission implements ClanPermission {

    ADMINISTRATOR,
    INVITE_MEMBERS,
    KICK_MEMBERS,
    PROMOTE_MEMBER,
    MANAGE_SETTINGS,
    DISBAND_CLAN,
    RENAME_DISPLAY,
    CHANGE_PVP_MODE,
    CHANGE_INVITE_STATE,
    MANAGE_DIPLOMACY
    ;

    @Override
    public @NotNull String namespace() {
        return "bytephoria";
    }

    @Override
    public @NotNull String key() {
        return this.name().toLowerCase();
    }
}
