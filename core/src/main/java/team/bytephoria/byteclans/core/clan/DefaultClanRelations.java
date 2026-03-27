package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import team.bytephoria.byteclans.api.ClanRelation;
import team.bytephoria.byteclans.api.ClanRelationType;
import team.bytephoria.byteclans.api.ClanRelations;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class DefaultClanRelations implements ClanRelations {

    private final Map<UUID, ClanRelation> relations = new ConcurrentHashMap<>();

    @Override
    public @NotNull @UnmodifiableView Collection<ClanRelation> alls() {
        return Collections.unmodifiableCollection(this.relations.values());
    }

    @Override
    public @NotNull @UnmodifiableView Collection<ClanRelation> allies() {
        return this.collectIfRelation(ClanRelationType.ALLIANCE);
    }

    @Override
    public @NotNull @Unmodifiable Collection<ClanRelation> enemies() {
        return this.collectIfRelation(ClanRelationType.ENEMY);
    }

    @Override
    public @NotNull @UnmodifiableView Collection<ClanRelation> tensions() {
        return this.collectIfRelation(ClanRelationType.TENSION);
    }

    @Override
    public void add(final @NotNull ClanRelation clanRelation) {
        this.relations.put(clanRelation.clanUniqueId(), clanRelation);
    }

    @Override
    public ClanRelation remove(final @NotNull UUID clanUniqueId) {
        return this.relations.remove(clanUniqueId);
    }

    @Override
    public boolean isAlly(final @NotNull UUID clanUniqueId) {
        return this.getRelationType(clanUniqueId) == ClanRelationType.ALLIANCE;
    }

    @Override
    public boolean isEnemy(final @NotNull UUID clanUniqueId) {
        return this.getRelationType(clanUniqueId) == ClanRelationType.ENEMY;
    }

    @Override
    public ClanRelation getRelation(final @NotNull UUID clanUniqueId) {
        return this.relations.get(clanUniqueId);
    }

    @Override
    public ClanRelationType getRelationType(final @NotNull UUID clanUniqueId) {
        final ClanRelation clanRelation = this.relations.get(clanUniqueId);
        return clanRelation != null ? clanRelation.type() : ClanRelationType.NEUTRAL;
    }

    @NotNull
    @UnmodifiableView
    Collection<ClanRelation> collectIfRelation(final @NotNull ClanRelationType clanRelationType) {
        return this.collectIf(clanRelation -> clanRelation.type() == clanRelationType);
    }

    @NotNull
    @UnmodifiableView
    Collection<ClanRelation> collectIf(final @NotNull Predicate<ClanRelation> predicate) {
        final List<ClanRelation> list = new ArrayList<>();
        for (final ClanRelation relation : this.relations.values()) {
            if (predicate.test(relation)) {
                list.add(relation);
            }
        }

        return Collections.unmodifiableCollection(list);
    }

}
