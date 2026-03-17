package team.bytephoria.byteclans.bukkitapi.event.create;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;

public final class ClanPostCreateAsyncEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final BukkitClanPlayer clanPlayer;
    private final ClanMember clanMember;
    private final Clan clan;

    public ClanPostCreateAsyncEvent(
            final @NotNull BukkitClanPlayer clanPlayer,
            final @NotNull ClanMember clanMember,
            final @NonNull Clan clan
    ) {
        super(true);
        this.clanPlayer = clanPlayer;
        this.clanMember = clanMember;
        this.clan = clan;
    }

    public Clan clan() {
        return this.clan;
    }

    public ClanMember clanMember() {
        return this.clanMember;
    }

    public BukkitClanPlayer clanPlayer() {
        return this.clanPlayer;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
