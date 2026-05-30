package team.bytephoria.byteclans.platform.spigot.command;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.spigot.SpigotPlugin;

public final class ClanMenuCommand {

    private final SpigotPlugin spigotPlugin;
    public ClanMenuCommand(final @NotNull SpigotPlugin spigotPlugin) {
        this.spigotPlugin = spigotPlugin;
    }

    @Command("clan")
    public void clan(final @NotNull Player player) {
        assert this.spigotPlugin.zMenuHook().menuPlugin() != null;
        this.spigotPlugin.zMenuHook().menuPlugin().getInventoryManager().openInventory(player, "clan");
    }

}
