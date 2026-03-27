package team.bytephoria.byteclans.spi.storage.view;

import java.util.UUID;

public record ClanTensionView(
        UUID enemyClanUniqueId,
        String enemyClanName,
        UUID sourceClanUniqueId,
        String sourceClanName
) {}