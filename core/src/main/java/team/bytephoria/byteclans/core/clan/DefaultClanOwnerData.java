package team.bytephoria.byteclans.core.clan;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanOwnerData;

import java.util.UUID;

public record DefaultClanOwnerData(String name, UUID uniqueId) implements ClanOwnerData {

    @Contract("_ -> new")
    public static @NonNull ClanOwnerData from(final @NotNull ClanMember clanMember) {
        return new  DefaultClanOwnerData(clanMember.name(), clanMember.uniqueId());
    }

}
