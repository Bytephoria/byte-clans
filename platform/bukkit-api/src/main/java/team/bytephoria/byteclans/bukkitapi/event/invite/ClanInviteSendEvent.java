package team.bytephoria.byteclans.bukkitapi.event.invite;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;

public final class ClanInviteSendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember senderMember;

    private final Clan targetClan;
    private final BukkitClanPlayer target;

    private boolean cancelled;

    public ClanInviteSendEvent(
            final @NotNull ClanMember senderMember,
            final @NotNull Clan targetClan,
            final @NotNull BukkitClanPlayer target
    ) {
        this.senderMember = senderMember;
        this.targetClan = targetClan;
        this.target = target;

        this.cancelled = false;
    }

    public ClanMember senderMember() {
        return this.senderMember;
    }

    public Clan targetClan() {
        return this.targetClan;
    }

    public BukkitClanPlayer target() {
        return this.target;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public static  HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
