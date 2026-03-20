package team.bytephoria.byteclans.platform.commonbukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.bukkitapi.event.create.ClanPostCreateAsyncEvent;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.Configuration;

public final class ClanPostCreateAsyncListener implements Listener {

    private final Configuration configuration;
    public ClanPostCreateAsyncListener(final @NotNull Configuration configuration) {
        this.configuration = configuration;
    }

    @EventHandler
    public void onClanPostCreateAsyncEvent(final @NotNull ClanPostCreateAsyncEvent asyncEvent) {
        final Clan clan = asyncEvent.clan();
        final String finalDisplayName = this.configuration.clan().defaults().display()
                .replace("{clan}", clan.data().name());

        clan.data().displayName(finalDisplayName);
    }

}
