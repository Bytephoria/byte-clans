package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface ClanRelations {

    Collection<ClanRelation> alls();

    Collection<ClanRelation> allies();

    Collection<ClanRelation> enemies();

    Collection<ClanRelation> tensions();

    void add(final @NotNull ClanRelation clanRelation);

    ClanRelation remove(final @NotNull UUID clanUniqueId);

    boolean isAlly(final @NotNull UUID clanUniqueId);
    boolean isEnemy(final @NotNull UUID clanUniqueId);

    ClanRelation getRelation(final @NotNull UUID clanUniqueId);
    ClanRelationType getRelationType(final @NotNull UUID clanUniqueId);

}
