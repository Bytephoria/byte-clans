package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.result.ClanCreateResult;
import team.bytephoria.byteclans.api.result.ClanDisbandResult;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;

import java.util.UUID;

public interface ClanManager {

    interface Admin {

        ResponseContext<Clan, ClanDisbandResult> disbandClanByName(
                final @NotNull String clanName
        );

        ResponseContext<Clan, ClanDisbandResult> disbandClanByUniqueId(
                final @NotNull UUID uniqueId
        );

    }

    Admin admin();

    ResponseContext<Clan, ClanCreateResult> createClan(
            final @NotNull ClanPlayer clanPlayer,
            final @NotNull String clanName
    );

    ResponseContext<Clan, ClanDisbandResult> disbandClan(
            final @NotNull ClanPlayer clanPlayer
    );

    ResponseContext<Clan, ClanDisbandResult> disbandClan(
            final @NotNull ClanMember clanMember
    );

}
