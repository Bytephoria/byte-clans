package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.manager.ClanSettingsManager;

public final class SetClanDisplayAction extends Action {

    private final String display;
    public SetClanDisplayAction(final @NotNull String display) {
        this.display = display;
    }

    @Override
    protected void execute(
            final @NotNull Player player,
            final @NotNull Button button,
            final @NotNull InventoryEngine inventoryEngine,
            final @NotNull Placeholders placeholders
    ) {

        final ByteClans byteClans = ByteClans.getAPI();
        final ClanSettingsManager clanSettingsManager = byteClans.settingsManager();

        byteClans.getMember(player.getUniqueId())
                .ifPresent(clanMember -> {
                    final String finalDisplay = this.display
                            .replace("%clan_name%", clanMember.clan().data().name());

                    clanSettingsManager.renameDisplay(clanMember, finalDisplay, ByteClans.getAPI().globalSettings().displayNameChangeCooldown());
                });

    }
}
