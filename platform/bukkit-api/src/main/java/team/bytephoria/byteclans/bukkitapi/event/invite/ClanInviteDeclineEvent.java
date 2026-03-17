package team.bytephoria.byteclans.bukkitapi.event.invite;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanInvitation;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;

public final class ClanInviteDeclineEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final BukkitClanPlayer player;
    private final ClanInvitation invitation;

    public ClanInviteDeclineEvent(
            final @NotNull BukkitClanPlayer player,
            final @NotNull ClanInvitation invitation
    ) {
        this.player = player;
        this.invitation = invitation;
    }

    public BukkitClanPlayer player() {
        return this.player;
    }

    public ClanInvitation invitation() {
        return this.invitation;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
