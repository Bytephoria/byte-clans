package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.*;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public final class DefaultClanMember implements ClanMember {

    private final UUID userUniqueId;
    private final String userName;

    private final ClanMemberData clanMemberData;
    private final ClanMemberStatistics clanMemberStatistics;

    private Clan clan;
    private ClanRole clanRole;
    private ClanPlayer clanPlayer;
    private ClanChatType chatType;

    public DefaultClanMember(
            final @NotNull UUID userUniqueId,
            final @NotNull String userName,
            final @NotNull ClanMemberData clanMemberData,
            final @NotNull Clan clan,
            final @NotNull ClanRole clanRole,
            final @NotNull ClanChatType clanChatType,
            final @NotNull ClanMemberStatistics clanMemberStatistics
    ) {
        this.userUniqueId = userUniqueId;
        this.userName = userName;
        this.clanMemberData = clanMemberData;
        this.clan = clan;
        this.clanRole = clanRole;
        this.chatType = clanChatType;
        this.clanMemberStatistics = clanMemberStatistics;
    }

    public DefaultClanMember(
            final @NotNull ClanPlayer clanPlayer,
            final @Nullable Clan clan,
            final @NotNull ClanMemberData clanMemberData,
            final @NotNull ClanRole clanRole,
            final @NotNull ClanChatType chatType,
            final @NotNull ClanMemberStatistics clanMemberStatistics
    ) {
        this.userUniqueId = clanPlayer.uniqueId();
        this.userName = clanPlayer.name();
        this.clanPlayer = clanPlayer;
        this.clanRole = clanRole;
        this.clanMemberData = clanMemberData;
        this.clan = clan;
        this.chatType = chatType;
        this.clanMemberStatistics = clanMemberStatistics;
    }

    public DefaultClanMember(
            final @NotNull UUID userUniqueId,
            final @NotNull String userName,
            final @NotNull Clan clan,
            final @NotNull ClanMemberData clanMemberData,
            final @NotNull ClanRole clanRole,
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanChatType chatType,
            final @NotNull ClanMemberStatistics clanMemberStatistics
    ) {
        this.userUniqueId = userUniqueId;
        this.userName = userName;
        this.clanRole = clanRole;
        this.clanMemberData = clanMemberData;
        this.clan = clan;
        this.clanPlayer = clanPlayer;
        this.chatType = chatType;
        this.clanMemberStatistics = clanMemberStatistics;
    }

    public DefaultClanMember(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanMemberData clanMemberData,
            final @NotNull ClanRole clanRole,
            final @NotNull ClanMemberStatistics clanMemberStatistics
    ) {
        this(clanPlayer, null, clanMemberData, clanRole, ClanChatType.PUBLIC, clanMemberStatistics);
    }

    @Override
    public UUID uniqueId() {
        return this.userUniqueId;
    }

    @Override
    public String name() {
        return this.userName;
    }

    @Contract(pure = true)
    @Override
    public @NonNull Optional<ClanPlayer> player() {
        return Optional.ofNullable(this.clanPlayer);
    }

    @Override
    public void player(final @Nullable ClanPlayer clanPlayer) {
        this.clanPlayer = clanPlayer;
    }

    @Override
    public Clan clan() {
        return this.clan;
    }

    public void clan(final @NotNull Clan clan) {
        this.clan = clan;
    }

    @Override
    public @NotNull ClanRole role() {
        return this.clanRole;
    }

    @Override
    public ClanMemberData data() {
        return this.clanMemberData;
    }

    @Override
    public ClanChatType chatType() {
        return this.chatType;
    }

    @Override
    public ClanMemberStatistics statistics() {
        return this.clanMemberStatistics;
    }

    @Override
    public void role(final @NotNull ClanRole clanRole) {
        this.clanRole = clanRole;
    }

    @Override
    public void chatType(final @NotNull ClanChatType clanChatType) {
        this.chatType = clanChatType;
    }

    @Override
    public boolean hasPermission(final @NotNull ClanAction clanAction) {
        final EnumSet<ClanAction> clanActions = this.role().actions();
        if (clanActions.contains(ClanAction.ADMINISTRATOR)) {
            return true;
        }

        return clanActions.contains(clanAction);
    }

    @Override
    public boolean isOnline() {
        return this.player().isPresent();
    }

}
