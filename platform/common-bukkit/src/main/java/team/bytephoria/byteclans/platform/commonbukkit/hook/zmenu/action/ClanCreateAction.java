package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclans.platform.commonbukkit.CommonBukkitFacade;
import team.bytephoria.byteclans.platform.commonbukkit.messages.Messenger;

import java.util.Locale;
import java.util.Map;

public final class ClanCreateAction extends Action {

    private final CommonBukkitFacade commonBukkitFacade;
    private final Messenger messenger;

    public ClanCreateAction(
            final @NotNull CommonBukkitFacade commonBukkitFacade,
            final @NotNull Messenger messenger
    ) {
        this.commonBukkitFacade = commonBukkitFacade;
        this.messenger = messenger;
    }

    @Override
    protected void execute(
            final @NotNull Player player,
            final @NotNull Button button,
            final @NotNull InventoryEngine inventoryEngine,
            final @NotNull Placeholders placeholders
    ) {

        this.commonBukkitFacade.chatInput().register(
                player,
                clanName -> this.createClan(player, clanName),
                () -> this.messenger.sendPathMessage(player, "clan.create.timeout")
        );
    }

    private void createClan(
            final @NotNull Player player,
            final @NotNull String clanName
    ) {

        final ClanPlayer bukkitClanPlayer = BukkitClanPlayer.wrap(player);
        this.commonBukkitFacade.supplyAsync(() -> ByteClans.getAPI()
                        .clanManager()
                        .createClan(bukkitClanPlayer, clanName))
                .thenAccept(response ->
                        this.commonBukkitFacade.mainThreadExecutor().execute(() -> {
                            final String resultPath = "clan.create." + response.result()
                                    .name()
                                    .toLowerCase(Locale.ROOT)
                                    .replace('_', '-');

                            this.messenger.sendPathMessage(player, resultPath, Map.of("clan", clanName));
                        }));

    }

}