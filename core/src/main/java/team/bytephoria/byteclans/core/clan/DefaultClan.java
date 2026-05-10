package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.*;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.*;
import team.bytephoria.byteclans.api.util.IntValue;

import java.util.*;

public final class DefaultClan implements Clan {

    private final Map<UUID, ClanMember> members;

    private final UUID uniqueId;
    private final ClanData clanData;
    private final ClanSettings clanSettings;
    private final ClanStatistics clanStatistics;
    private final ClanRelations clanRelations;
    private final IntValue clanPoints;

    private ClanOwnerData ownerData;
    private ClanMember owner;

    public DefaultClan(
            final @NotNull UUID clanUniqueId,
            final @NotNull ClanOwnerData ownerData,
            final @Nullable ClanMember ownerClanMember,
            final @NotNull ClanData clanData,
            final @NotNull ClanSettings clanSettings,
            final @NotNull ClanStatistics clanStatistics,
            final @NotNull ClanRelations clanRelations,
            final @NotNull IntValue clanPoints
    ) {
        this.uniqueId = clanUniqueId;
        this.ownerData = ownerData;
        this.owner = ownerClanMember;
        this.clanData = clanData;
        this.clanSettings = clanSettings;
        this.clanStatistics = clanStatistics;
        this.members = new HashMap<>();
        this.clanRelations = clanRelations;
        this.clanPoints = clanPoints;
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
    public @NotNull @Unmodifiable Collection<ClanMember> members() {
        return Collections.unmodifiableCollection(this.members.values());
    }

    @Override
    public @UnmodifiableView @NotNull Collection<ClanMember> allMembers() {
        final Collection<ClanMember> members = this.getAllMembers();
        return Collections.unmodifiableCollection(members);
    }

    @Override
    public @UnmodifiableView @NotNull Collection<ClanMember> onlineMembers() {
        return this.members.values().stream()
                .filter(ClanMember::isOnline)
                .toList();
    }

    @Override
    public @UnmodifiableView @NotNull Collection<ClanMember> onlineAllMembers() {
        return this.getAllMembers().stream()
                .filter(ClanMember::isOnline)
                .toList();
    }

    private @NotNull Collection<ClanMember> getAllMembers() {
        final Collection<ClanMember> members = new ArrayList<>(this.members.values());
        if (this.owner != null) {
            members.add(this.owner);
        }

        return members;
    }

    @Override
    public @NonNull IntValue points() {
        return this.clanPoints;
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
        if (this.owner != null && this.owner.uniqueId().equals(uniqueId)) {
            return this.owner;
        }

        return this.members.get(uniqueId);
    }

    @Override
    public boolean isMember(final @NotNull UUID uniqueId) {
        return this.isOwner(uniqueId) || this.members.containsKey(uniqueId);
    }

    @Override
    public boolean isMember(final @NotNull ClanPlayer clanPlayer) {
        return this.isMember(clanPlayer.uniqueId());
    }

    @Override
    public boolean isOwner(final @NotNull UUID uniqueId) {
        return this.ownerData.uniqueId().equals(uniqueId);
    }

    @Override
    public boolean isOwner(final @NotNull ClanPlayer clanPlayer) {
        return this.isOwner(clanPlayer.uniqueId());
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
