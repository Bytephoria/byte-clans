package team.bytephoria.byteclans.platform.commonbukkit.command;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.ZMenuHook;

public final class ClanMenuCommand {

    private final ZMenuHook zMenuHook;

    public ClanMenuCommand(final @NotNull ZMenuHook zMenuHook) {
        this.zMenuHook = zMenuHook;
    }

    @Command("clan")
    public void clan(final @NotNull Player player) {
        assert this.zMenuHook.menuPlugin() != null;
        this.zMenuHook.menuPlugin().getInventoryManager().openInventory(player, "clan");
    }

}
