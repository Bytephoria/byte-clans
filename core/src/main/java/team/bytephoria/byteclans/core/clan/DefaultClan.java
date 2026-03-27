package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.*;

import java.util.*;

public final class DefaultClan implements Clan {

    private final Map<UUID, ClanMember> members;

    private final UUID uniqueId;
    private final ClanData clanData;
    private final ClanSettings clanSettings;
    private final ClanStatistics clanStatistics;
    private final ClanRelations clanRelations;

    private ClanOwnerData ownerData;
    private ClanMember owner;

    public DefaultClan(
            final @NotNull UUID clanUniqueId,
            final @NotNull ClanOwnerData ownerData,
            final @Nullable ClanMember ownerClanMember,
            final @NotNull ClanData clanData,
            final @NotNull ClanSettings clanSettings,
            final @NotNull ClanStatistics clanStatistics,
            final @NotNull ClanRelations clanRelations
    ) {
        this.uniqueId = clanUniqueId;
        this.ownerData = ownerData;
        this.owner = ownerClanMember;
        this.clanData = clanData;
        this.clanSettings = clanSettings;
        this.clanStatistics = clanStatistics;
        this.members = new HashMap<>();
        this.clanRelations = clanRelations;
    }

    @Override
    public UUID uniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NotNull ClanOwnerData ownerData() {
        return this.ownerData;
    }

    @Override
    public @NotNull Optional<ClanMember> ownerMember() {
        return Optional.ofNullable(this.owner);
    }

    @Override
    public @NonNull ClanData data() {
        return this.clanData;
    }

    @Override
    public @NotNull ClanSettings settings() {
        return this.clanSettings;
    }

    @Override
    public @NonNull ClanStatistics statistics() {
        return this.clanStatistics;
    }

    @Override
    public @NotNull ClanRelations relations() {
        return this.clanRelations;
    }

    @Override
    public @NotNull @Unmodifiable List<ClanMember> members() {
        final List<ClanMember> list = new ArrayList<>(this.members.values());
        return Collections.unmodifiableList(list);
    }

    @Override
    public @UnmodifiableView @NotNull Collection<ClanMember> allMembers() {
        final List<ClanMember> members = new ArrayList<>(this.members.values());
        if (this.owner != null) {
            members.add(this.owner);
        }

        return Collections.unmodifiableCollection(members);
    }

    @Override
    public void ownerData(final @NotNull ClanOwnerData clanOwnerData) {
        this.ownerData = clanOwnerData;
    }

    @Override
    public void ownerMember(final @NotNull ClanMember clanMember) {
        this.owner = clanMember;
    }

    @Override
    public ClanMember getMemberByUniqueId(final @NotNull UUID uniqueId) {
        return this.members.get(uniqueId);
    }

    @Override
    public boolean isMember(final @NotNull UUID uniqueId) {
        return this.members.containsKey(uniqueId);
    }

    @Override
    public boolean isMember(final @NotNull ClanPlayer clanPlayer) {
        return this.isMember(clanPlayer.uniqueId());
    }

    @Override
    public void addMember(final @NotNull ClanMember clanMember) {
        this.members.put(clanMember.uniqueId(), clanMember);
    }

    @Override
    public ClanMember removeMemberByUniqueId(final @NotNull UUID uniqueId) {
        return this.members.remove(uniqueId);
    }
}
