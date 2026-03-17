package team.bytephoria.byteclans.bukkitapi.event.settings;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPvPMode;

public final class ClanPvPModeChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember clanMember;
    private final Clan clan;
    private final ClanPvPMode oldMode;
    private final ClanPvPMode newMode;

    private boolean cancelled;

    public ClanPvPModeChangeEvent(
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan,
            final @NotNull ClanPvPMode oldMode,
            final @NotNull ClanPvPMode newMode
    ) {
        this.clanMember = clanMember;
        this.clan = clan;
        this.oldMode = oldMode;
        this.newMode = newMode;
        this.cancelled = false;
    }

    public ClanMember clanMember() {
        return this.clanMember;
    }

    public Clan clan() {
        return this.clan;
    }

    public ClanPvPMode oldMode() {
        return this.oldMode;
    }

    public ClanPvPMode newMode() {
        return this.newMode;
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