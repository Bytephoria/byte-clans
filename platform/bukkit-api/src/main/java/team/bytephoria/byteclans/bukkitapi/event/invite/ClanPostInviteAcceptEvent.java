package team.bytephoria.byteclans.bukkitapi.event.invite;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanInvitation;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;

public final class ClanPostInviteAcceptEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final BukkitClanPlayer player;
    private final ClanInvitation invitation;
    private final ClanMember clanMember;
    private final Clan clan;

    public ClanPostInviteAcceptEvent(
            final @NotNull BukkitClanPlayer player,
            final @NotNull ClanInvitation invitation,
            final @NotNull ClanMember clanMember,
            final @NotNull Clan clan
    ) {
        this.player = player;
        this.invitation = invitation;
        this.clanMember = clanMember;
        this.clan = clan;
    }

    public BukkitClanPlayer player() {
        return this.player;
    }

    public ClanInvitation invitation() {
        return this.invitation;
    }

    public ClanMember clanMember() {
        return this.clanMember;
    }

    public Clan clan() {
        return this.clan;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}