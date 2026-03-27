package team.bytephoria.byteclans.spi.storage.view;

import java.util.UUID;

public record ClanAllyView(
        UUID clanUniqueId,
        String clanName
) {}