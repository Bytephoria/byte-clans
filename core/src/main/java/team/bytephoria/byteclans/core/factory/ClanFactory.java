package team.bytephoria.byteclans.core.factory;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.core.clan.*;
import team.bytephoria.byteclans.core.util.ClanNameUUID;
import team.bytephoria.byteclans.spi.storage.view.ClanView;

import java.time.Instant;
import java.util.UUID;

public final class ClanFactory {

    public @NotNull DefaultClan create(
            final @NotNull ClanMember owner,
            final @NotNull String clanName,
            final @NotNull ClanGlobalSettings globalSettings
    ) {

        final UUID assignedClanId = ClanNameUUID.from(clanName);
        return new DefaultClan(
                assignedClanId,
                new DefaultClanOwnerData(owner.name(), owner.uniqueId()),
                owner,
                new DefaultClanData(clanName, clanName, Instant.now()),
                new DefaultClanSettings(
                        globalSettings.defaultMaxMembers(),
                        globalSettings.defaultPvPMode(),
                        globalSettings.defaultInviteState()
                ),
                DefaultClanStatistics.allZero());
    }

    public @NotNull Clan create(final @NotNull ClanView clanView) {
        return new DefaultClan(
                clanView.clanUniqueId(),
                new DefaultClanOwnerData(clanView.ownerName(), clanView.ownerUniqueId()),
                null,
                new DefaultClanData(clanView.clanName(), clanView.clanDisplayName(), clanView.createdAt()),
                new DefaultClanSettings(clanView.maxMembers(), clanView.clanPvPMode(), clanView.clanInviteState()),
                new DefaultClanStatistics(clanView.kills(), clanView.deaths(), clanView.killsStreak()));
    }

}
