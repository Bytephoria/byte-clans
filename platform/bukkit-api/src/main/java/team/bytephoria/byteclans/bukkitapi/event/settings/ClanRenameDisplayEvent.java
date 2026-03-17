package team.bytephoria.byteclans.bukkitapi.event.settings;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;

public final class ClanRenameDisplayEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember clanMember;
    private final Clan clan;
    private final String oldDisplayName;
    private final String newDisplayName;

    private boolean cancelled;

    public ClanRenameDisplayEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull String oldDisplayName,
            final @NotNull String newDisplayName
    ) {
        this.clanMember = clanMember;
        this.clan = clan;
        this.oldDisplayName = oldDisplayName;
        this.newDisplayName = newDisplayName;
        this.cancelled = false;
    }

    public ClanMember clanMember() {
        return this.clanMember;
    }

    public Clan clan() {
        return this.clan;
    }

    public String oldDisplayName() {
        return this.oldDisplayName;
    }

    public String newDisplayName() {
        return this.newDisplayName;
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