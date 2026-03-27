package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.ClanRelation;
import team.bytephoria.byteclans.api.ClanRelationType;

import java.util.UUID;

public record DefaultClanRelation(
        UUID clanUniqueId,
        String clanName,
        ClanRelationType type,
        @Nullable UUID sourceClanUniqueId
) implements ClanRelation {

}
