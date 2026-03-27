package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ClanRelation {

    UUID clanUniqueId();

    String clanName();

    ClanRelationType type();

    @Nullable UUID sourceClanUniqueId();

}
