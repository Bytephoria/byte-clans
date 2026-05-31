package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanRelation;
import team.bytephoria.byteclans.api.access.ByteClans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ClanAllyButton extends PaginateButton {

    @Override
    public void onRender(final @NotNull Player player, final @NotNull InventoryEngine inventoryEngine) {
        ByteClans.getAPI().getMember(player.getUniqueId())
                .ifPresent(clanMember -> {
                    final Clan clan = clanMember.clan();
                    if (clan == null) {
                        return;
                    }

                    final Collection<ClanRelation> clanRelations = clan.relations().allies();
                    final List<ItemStack> itemStacks = new ArrayList<>(clanRelations.size());
                    final MenuItemStack menuItemStack = this.getItemStack();

                    for (final ClanRelation clanRelation : clanRelations) {
                        final Placeholders placeholders = new Placeholders();

                        placeholders.register("relation_name", clanRelation.clanName());
                        placeholders.register("relation_uuid", clanRelation.clanUniqueId().toString());

                        itemStacks.add(menuItemStack.build(player, true, placeholders));
                    }

                    this.paginate(itemStacks, inventoryEngine, inventoryEngine::addItem);
                });

    }

    @Override
    public int getPaginationSize(final @NotNull Player player) {
        return ByteClans.getAPI()
                .getMember(player.getUniqueId())
                .map(clanMember -> clanMember.clan().relations().allies().size())
                .orElse(0);
    }
}
