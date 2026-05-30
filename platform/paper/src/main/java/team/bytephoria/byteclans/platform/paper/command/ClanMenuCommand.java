package team.bytephoria.byteclans.platform.paper.command;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.paper.PaperPlugin;

public final class ClanMenuCommand {

    private final PaperPlugin paperPlugin;
    public ClanMenuCommand(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @Command("clan")
    public void clan(final @NotNull Player player) {
        assert this.paperPlugin.zMenuHook().menuPlugin() != null;
        this.paperPlugin.zMenuHook().menuPlugin().getInventoryManager().openInventory(player, "clan");
    }

}
