package team.bytephoria.byteclans.bukkitapi.event.diplomacy.request;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanMember;

import java.util.UUID;

public final class ClanAllyRequestDeclineEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ClanMember executorMember;
    private final UUID targetClanUniqueId;

    public ClanAllyRequestDeclineEvent(
            final @NotNull ClanMember executorMember,
            final  @NotNull UUID targetClanUniqueId
    ) {
        this.executorMember = executorMember;
        this.targetClanUniqueId = targetClanUniqueId;
    }

    public ClanMember executorMember() {
        return this.executorMember;
    }

    public UUID targetClanUniqueId() {
        return this.targetClanUniqueId;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
