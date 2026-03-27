package team.bytephoria.byteclans.spi.storage.view;

import java.util.Collection;

public record ClanViewWithRelations(
        ClanView clanView,
        Collection<ClanRelationView> relations
) {
}
