package team.bytephoria.byteclans.bukkitapi.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.util.IntValue;

public final class ClanPointsChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Clan clan;
    private final int value;
    private final int oldValue;
    private final int finalValue;
    private final IntValue.Operation operation;

    private boolean cancelled;

    public ClanPointsChangeEvent(
            final @NonNull Clan clan,
            final int value,
            final int oldValue,
            final int finalValue,
            final IntValue.Operation operation
    ) {
        this.clan = clan;
        this.value = value;
        this.oldValue = oldValue;
        this.finalValue = finalValue;
        this.operation = operation;
        this.cancelled = false;
    }

    public Clan clan() {
        return this.clan;
    }

    public int value() {
        return this.value;
    }

    public int oldValue() {
        return this.oldValue;
    }

    public int finalValue() {
        return this.finalValue;
    }

    public IntValue.Operation operation() {
        return this.operation;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
}
