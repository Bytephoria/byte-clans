package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import team.bytephoria.byteclans.api.util.Identity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface Clan extends Identity {

    @NotNull ClanOwnerData ownerData();

    @NotNull Optional<ClanMember> ownerMember();

    @NotNull ClanData data();

    @NotNull ClanSettings settings();

    @NotNull ClanStatistics statistics();

    @NotNull ClanRelations relations();

    @UnmodifiableView
    Collection<ClanMember> members();

    @UnmodifiableView
    Collection<ClanMember> allMembers();

    void ownerData(final @NotNull ClanOwnerData clanOwnerData);
    void ownerMember(final @NotNull ClanMember clanMember);

    ClanMember getMemberByUniqueId(final @NotNull UUID uniqueId);

    boolean isMember(final @NotNull UUID uniqueId);
    boolean isMember(final @NotNull ClanPlayer clanPlayer);

    void addMember(final @NotNull ClanMember clanMember);

    ClanMember removeMemberByUniqueId(final @NotNull UUID uniqueId);

    default boolean isMembersFull() {
        return this.members().size() >= this.settings().maxMembers();
    }

}
