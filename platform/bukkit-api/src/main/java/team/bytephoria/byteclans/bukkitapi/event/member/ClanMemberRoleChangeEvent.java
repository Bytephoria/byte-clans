package team.bytephoria.byteclans.bukkitapi.event.member;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanRole;

public final class ClanMemberRoleChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember changer;
    private final ClanMember changed;
    private final ClanRole oldRole;
    private final ClanRole newRole;
    private final Clan clan;

    private boolean cancelled;

    public ClanMemberRoleChangeEvent(
            final @NotNull ClanMember changer,
            final @NotNull ClanMember changed,
            final @NotNull ClanRole oldRole,
            final @NotNull ClanRole newRole,
            final @NotNull Clan clan
    ) {
        this.changer = changer;
        this.changed = changed;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.clan = clan;
        this.cancelled = false;
    }

    public ClanMember changer() {
        return this.changer;
    }

    public ClanMember changed() {
        return this.changed;
    }

    public ClanRole oldRole() {
        return this.oldRole;
    }

    public ClanRole newRole() {
        return this.newRole;
    }

    public Clan clan() {
        return this.clan;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}