package team.bytephoria.byteclans.bukkitapi.event.diplomacy;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;

import java.util.UUID;

public final class ClanTensionAddEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final UUID affectedClanUniqueId;
    private final String affectedClanName;
    private final Clan affectedClan;

    private final Clan targetClan;
    private final Clan sourceClan;

    public ClanTensionAddEvent(
            final @NotNull UUID affectedClanUniqueId,
            final @NotNull String affectedClanName,
            final @Nullable Clan affectedClan,
            final @NotNull Clan targetClan,
            final @NotNull Clan sourceClan
    ) {
        this.affectedClanUniqueId = affectedClanUniqueId;
        this.affectedClanName = affectedClanName;
        this.affectedClan = affectedClan;
        this.targetClan = targetClan;
        this.sourceClan = sourceClan;
    }

    public UUID affectedClanUniqueId() {
        return this.affectedClanUniqueId;
    }

    public String affectedClanName() {
        return this.affectedClanName;
    }

    public @Nullable Clan affectedClan() {
        return this.affectedClan;
    }

    public Clan targetClan() {
        return this.targetClan;
    }

    public Clan sourceClan() {
        return this.sourceClan;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}