package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.access.ByteClans;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class ClanListButton extends PaginateButton {


    public static final DateTimeFormatter COMPLETE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    @Override
    public void onRender(final @NotNull Player player, final @NotNull InventoryEngine inventoryEngine) {
        final List<ItemStack> itemStacks = new ArrayList<>(ByteClans.getAPI().loadedClans().size());

        for (final Clan clan : ByteClans.getAPI().loadedClans()) {
            final MenuItemStack menuItemStack = this.getItemStack();
            final Placeholders placeholders = new Placeholders();

            placeholders.register("clan_name", clan.data().name());
            placeholders.register("clan_display", clan.data().displayName());
            placeholders.register("clan_members", Integer.toString(clan.members().size()));
            placeholders.register("clan_created_at", COMPLETE_FORMATTER.format(clan.data().createdAt()));
            placeholders.register("clan_statistics_kills", clan.statistics().kills().toString());
            placeholders.register("clan_statistics_deaths", clan.statistics().deaths().toString());

            itemStacks.add(menuItemStack.build(player, true, placeholders));
        }

        this.paginate(itemStacks, inventoryEngine, inventoryEngine::addItem);
    }

    @Override
    public int getPaginationSize(final @NotNull Player player) {
        return ByteClans.getAPI().loadedClans().size();
    }
}
