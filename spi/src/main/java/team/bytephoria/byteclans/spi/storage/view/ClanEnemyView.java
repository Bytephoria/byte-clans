package team.bytephoria.byteclans.spi.storage.view;

import java.util.UUID;

public record ClanEnemyView(
        UUID clanUniqueId,
        String clanName
) {}