package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.commonbukkit.CommonBukkitFacade;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader.ClanCreateActionLoader;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader.SetClanDisplayActionLoader;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader.SetClanInviteStateActionLoader;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader.SetClanPvPModeActionLoader;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons.loader.ClanAllyButtonLoader;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons.loader.ClanEnemyButtonLoader;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons.loader.ClanListButtonLoader;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons.loader.ClanMemberButtonLoader;
import team.bytephoria.byteclans.platform.commonbukkit.messages.Messenger;

import java.io.File;

public final class ZMenuHook {

    private static final String[] DEFAULT_PATTERNS = {
            "zmenu/patterns/decoration.yml"
    };

    private static final String[] DEFAULT_INVENTORIES = {
            "zmenu/inventories/no_clan.yml",
            "zmenu/inventories/clan.yml",
            "zmenu/inventories/clan_allies.yml",
            "zmenu/inventories/clan_display.yml",
            "zmenu/inventories/clan_display_presets.yml",
            "zmenu/inventories/clan_enemies.yml",
            "zmenu/inventories/clan_member_list.yml",
            "zmenu/inventories/clan_settings.yml",
            "zmenu/inventories/clan_statistics.yml",
            "zmenu/inventories/clan_list.yml"
    };

    private final JavaPlugin javaPlugin;
    private final MenuPlugin menuPlugin;
    private final CommonBukkitFacade commonBukkitFacade;
    private final Messenger messenger;

    public ZMenuHook(
            final @NotNull JavaPlugin javaPlugin,
            final @NotNull CommonBukkitFacade commonBukkitFacade,
            final @NotNull Messenger messenger
    ) {
        this.javaPlugin = javaPlugin;

        this.menuPlugin = (MenuPlugin) Bukkit.getPluginManager().getPlugin("zMenu");
        if (this.menuPlugin == null) {
            throw new UnsupportedOperationException("zMenu plugin is not loaded");
        }

        this.commonBukkitFacade = commonBukkitFacade;
        this.messenger = messenger;
    }

    public void loadAll() {
        this.registerDefaultActions();
        this.registerDefaultButtons();

        this.loadPatterns();
        this.loadInventories();
    }

    public void reload() {
        this.menuPlugin.getInventoryManager().deleteInventories(this.javaPlugin);

        this.loadPatterns();
        this.loadInventories();
    }

    public void unloadAll() {
        this.menuPlugin.getInventoryManager().deleteInventories(this.javaPlugin);
        this.menuPlugin.getButtonManager().unregisters(this.javaPlugin);
    }

    public void registerDefaultActions() {
        this.menuPlugin.getButtonManager().registerAction(
                new SetClanDisplayActionLoader()
        );

        this.menuPlugin.getButtonManager().registerAction(new ClanCreateActionLoader(
                this.commonBukkitFacade,
                this.messenger
        ));

        this.menuPlugin.getButtonManager().registerAction(new SetClanPvPModeActionLoader());
        this.menuPlugin.getButtonManager().registerAction(new SetClanInviteStateActionLoader());
    }

    public void registerDefaultButtons() {
        this.menuPlugin.getButtonManager().unregisters(this.javaPlugin);

        this.menuPlugin.getButtonManager().register(new ClanListButtonLoader(this.javaPlugin));
        this.menuPlugin.getButtonManager().register(new ClanMemberButtonLoader(this.javaPlugin));
        this.menuPlugin.getButtonManager().register(new ClanAllyButtonLoader(this.javaPlugin));
        this.menuPlugin.getButtonManager().register(new ClanEnemyButtonLoader(this.javaPlugin));
    }

    public void loadPatterns() {
        this.loadDirectory(
                "zmenu/patterns",
                DEFAULT_PATTERNS,
                "Patterns",
                file -> this.menuPlugin.getPatternManager().loadPattern(file)
        );
    }

    public void loadInventories() {
        this.loadDirectory(
                "zmenu/inventories",
                DEFAULT_INVENTORIES,
                "Inventories",
                file -> this.menuPlugin.getInventoryManager().loadInventory(this.javaPlugin, file)
        );
    }

    private void loadDirectory(
            final @NotNull String path,
            final @NotNull String[] defaults,
            final @NotNull String name,
            final @NotNull ThrowingConsumer<File> consumer
    ) {
        final File directory = new File(this.javaPlugin.getDataFolder(), path);
        if (directory.mkdirs()) {
            for (final String resource : defaults) {
                this.javaPlugin.saveResource(resource, false);
            }

            this.javaPlugin.getLogger().info(name + " directory was created.");
        }

        final File[] files = directory.listFiles((file, fileName) ->
                fileName.toLowerCase().endsWith(".yml")
        );

        if (files == null) {
            return;
        }

        for (final File file : files) {
            try {
                consumer.accept(file);
            } catch (InventoryException exception) {
                this.javaPlugin.getLogger().severe(exception.getMessage());
            }
        }
    }

    public MenuPlugin menuPlugin() {
        return this.menuPlugin;
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {

        void accept(T value) throws InventoryException;
    }
}