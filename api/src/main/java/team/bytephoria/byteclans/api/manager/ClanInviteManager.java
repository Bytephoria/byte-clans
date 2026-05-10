package team.bytephoria.byteclans.api.manager;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanInvitation;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.result.ClanInviteAcceptResult;
import team.bytephoria.byteclans.api.result.ClanInviteDeclineResult;
import team.bytephoria.byteclans.api.result.ClanInviteSendResult;
import team.bytephoria.byteclans.api.util.response.context.ResponseContext;

public interface ClanInviteManager {

    @NotNull ResponseContext<ClanInvitation, ClanInviteSendResult> send(
            final @NotNull ClanMember senderMember,
            final @NotNull ClanPlayer targetPlayer
    );

    @NotNull ResponseContext<ClanInvitation, ClanInviteAcceptResult> accept(
            final @NotNull ClanPlayer clanPlayer
    );

    @NotNull ResponseContext<ClanInvitation, ClanInviteDeclineResult> decline(
            final @NotNull ClanPlayer clanPlayer
    );

}
