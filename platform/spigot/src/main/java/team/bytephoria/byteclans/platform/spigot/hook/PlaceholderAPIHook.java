package team.bytephoria.byteclans.platform.spigot.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.core.ApplicationFacade;
import team.bytephoria.byteclans.platform.spigot.SpigotPlugin;
import team.bytephoria.byteclans.platform.spigot.util.StringUtil;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

public final class PlaceholderAPIHook extends PlaceholderExpansion {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US);
    private static final DateTimeFormatter COMPLETE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

    private final SpigotPlugin spigotPlugin;
    public PlaceholderAPIHook(final @NotNull SpigotPlugin spigotPlugin) {
        this.spigotPlugin = spigotPlugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.spigotPlugin.getDescription().getName()
                .toLowerCase(Locale.ROOT)
                .replace("-", "");
    }

    @Override
    public @NotNull String getAuthor() {
        return this.spigotPlugin.getDescription().getAuthors().getFirst();
    }

    @Override
    public @NotNull String getVersion() {
        return this.spigotPlugin.getDescription().getVersion();
    }

    @Override
    public @NotNull String onPlaceholderRequest(final Player player, final @NotNull String params) {
        if (player == null) {
            return "";
        }

        final String[] arguments = StringUtil.split(params, '_');
        if (arguments.length < 2) {
            return "";
        }

        final String category = arguments[0];

        return switch (category) {
            case "clan" -> this.handleClan(player, arguments[1]);
            case "member" -> this.handleMember(player, arguments[1]);
            default -> "";
        };
    }

    private @NotNull String handleClan(final @NotNull Player player, final @NotNull String param) {
        return switch (param) {
            case "name" -> this.getClanOrEmpty(player, clan ->
                    clan.data().name()
            );

            case "display" -> this.getClanOrEmpty(player, clan ->
                    clan.data().displayName()
            );

            case "owner" -> this.getClanOrEmpty(player, clan ->
                    clan.ownerData().name()
            );

            case "members" -> this.getClanOrEmpty(player, clan ->
                    Integer.toString(clan.allMembers().size())
            );

            case "max-members" -> this.getClanOrEmpty(player, clan ->
                    Integer.toString(clan.settings().maxMembers())
            );

            case "kills" -> this.getClanOrEmpty(player, clan ->
                    Integer.toString(clan.statistics().kills())
            );

            case "deaths" -> this.getClanOrEmpty(player, clan ->
                    Integer.toString(clan.statistics().deaths())
            );

            case "kdr" -> this.getClanOrEmpty(player, clan ->
                    String.format("%.2f", clan.statistics().kdr())
            );

            case "kills-streak" -> this.getClanOrEmpty(player, clan ->
                    Integer.toString(clan.statistics().killsStreak())
            );

            case "pvp-mode" -> this.getClanOrEmpty(player, clan ->
                    clan.settings().pvpMode().name()
            );

            case "invite-state" -> this.getClanOrEmpty(player, clan ->
                    clan.settings().inviteState().name()
            );

            case "created-at" -> this.getClanOrEmpty(player, clan ->
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .withZone(ZoneId.systemDefault())
                            .format(clan.data().createdAt())
            );

            default -> "";
        };
    }

    private @NotNull String handleMember(final @NotNull Player player, final @NotNull String param) {
        return switch (param) {
            case "role" -> this.getMemberOrEmpty(player, member ->
                    member.role().id()
            );

            case "role-display" -> this.getMemberOrEmpty(player, member ->
                    member.role().displayName()
            );

            case "join-date" -> this.getMemberOrEmpty(player, member ->
                    FORMATTER
                            .withZone(ZoneId.systemDefault())
                            .format(member.data().joinedAt())
            );

            case "last-seen" -> this.getMemberOrEmpty(player, member ->
                    COMPLETE_FORMATTER
                            .withZone(ZoneId.systemDefault())
                            .format(member.data().lastSeenAt())
            );

            case "chat-mode" -> this.getMemberOrEmpty(player, member -> member.chatType().name());
            default -> "";
        };
    }

    private @NotNull String getMemberOrEmpty(
            final @NotNull Player player,
            final @NotNull Function<ClanMember, String> function
    ) {
        final ApplicationFacade applicationFacade = this.spigotPlugin.paperBootstrap().applicationFacade();
        final ClanMember clanMember = applicationFacade.clanMemberCache().get(player.getUniqueId());
        if (clanMember == null) {
            return "";
        }

        return function.apply(clanMember);
    }

    private @NotNull String getClanOrEmpty(
            final @NotNull Player player,
            final @NotNull Function<Clan, String> function
    ) {

        final ApplicationFacade applicationFacade = this.spigotPlugin.paperBootstrap().applicationFacade();
        final ClanMember clanMember = applicationFacade.clanMemberCache().get(player.getUniqueId());
        if (clanMember == null || clanMember.clan() == null) {
            return "";
        }

        return function.apply(clanMember.clan());
    }
}