package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.access.ByteClans;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons.ClanListButton.COMPLETE_FORMATTER;

public final class ClanMemberButton extends PaginateButton {

    @Override
    public void onRender(final @NotNull Player player, final @NotNull InventoryEngine inventoryEngine) {
        ByteClans.getAPI().getMember(player.getUniqueId())
                .ifPresent(clanMember -> {
                    final Clan clan = clanMember.clan();
                    if (clan == null) {
                        return;
                    }

                    final MenuItemStack menuItemStack = this.getItemStack();
                    final Placeholders placeholders = new Placeholders();

                    final Collection<ClanMember> members = clan.allMembers();
                    final List<ItemStack> itemStacks = new LinkedList<>();

                    for (final ClanMember member : members) {
                        placeholders.register("uuid", member.uniqueId().toString());
                        placeholders.register("name", member.name());
                        placeholders.register("role_id", member.role().id());
                        placeholders.register("role_display", member.role().displayName());
                        placeholders.register("joined_at", COMPLETE_FORMATTER.format(member.data().joinedAt()));
                        placeholders.register("last_seen_at", COMPLETE_FORMATTER.format(member.data().lastSeenAt()));
                        placeholders.register("kills", member.statistics().kills().toString());
                        placeholders.register("deaths", member.statistics().deaths().toString());
                        placeholders.register("kdr", String.format("%.2f", member.statistics().kdr()));

                        if (menuItemStack.getMaterial().equals(Material.PLAYER_HEAD.name())) {
                            final ItemStack itemStack = menuItemStack.build(player, true, placeholders);
                            final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

                            skullMeta.setPlayerProfile(Bukkit.createProfileExact(member.uniqueId(), member.name()));
                            itemStack.setItemMeta(skullMeta);

                            itemStacks.add(itemStack);
                        } else {
                            itemStacks.add(menuItemStack.build(player, true, placeholders));
                        }
                    }

                    this.paginate(itemStacks, inventoryEngine, inventoryEngine::addItem);
                });

    }

    @Override
    public int getPaginationSize(final @NotNull Player player) {
        return ByteClans.getAPI()
                .getMember(player.getUniqueId())
                .map(clanMember -> clanMember.clan().allMembers().size())
                .orElse(0);
    }
}
