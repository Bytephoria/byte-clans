package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.*;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class DefaultClanMember implements ClanMember {

    private final UUID userUniqueId;
    private final String userName;

    private final ClanMemberData clanMemberData;

    private Clan clan;
    private ClanRole clanRole;
    private ClanPlayer clanPlayer;
    private ClanChatType chatType;

    public DefaultClanMember(
            final @NotNull ClanPlayer clanPlayer,
            final @Nullable Clan clan,
            final @NotNull ClanMemberData clanMemberData,
            final @NotNull ClanRole clanRole,
            final @NotNull ClanChatType chatType
    ) {
        this.userUniqueId = clanPlayer.uniqueId();
        this.userName = clanPlayer.name();
        this.clanPlayer = clanPlayer;
        this.clanRole = clanRole;
        this.clanMemberData = clanMemberData;
        this.clan = clan;
        this.chatType = chatType;
    }

    public DefaultClanMember(
            final @NotNull UUID userUniqueId,
            final @NotNull String userName,
            final @NotNull Clan clan,
            final @NotNull ClanMemberData clanMemberData,
            final @NotNull ClanRole clanRole,
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanChatType chatType
    ) {
        this.userUniqueId = userUniqueId;
        this.userName = userName;
        this.clanRole = clanRole;
        this.clanMemberData = clanMemberData;
        this.clan = clan;
        this.clanPlayer = clanPlayer;
        this.chatType = chatType;
    }

    public DefaultClanMember(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull ClanMemberData clanMemberData,
            final @NotNull ClanRole clanRole
    ) {
        this(clanPlayer, null, clanMemberData, clanRole, ClanChatType.PUBLIC);
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

    public void player(final @NotNull ClanPlayer clanPlayer) {
        this.clanPlayer = Objects.requireNonNull(clanPlayer);
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

}
