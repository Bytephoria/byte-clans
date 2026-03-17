package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.util.Identity;

import java.util.Optional;

public interface ClanMember extends Identity {

    String name();

    Optional<ClanPlayer> player();

    Clan clan();

    ClanRole role();

    ClanMemberData data();

    ClanChatType chatType();

    void role(final @NotNull ClanRole clanRole);
    void chatType(final @NotNull ClanChatType clanChatType);

    boolean hasPermission(final @NotNull ClanAction clanAction);

}
