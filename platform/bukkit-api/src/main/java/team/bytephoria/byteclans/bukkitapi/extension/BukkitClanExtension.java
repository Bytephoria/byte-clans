package team.bytephoria.byteclans.bukkitapi.extension;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.extension.ClanExtension;
import team.bytephoria.byteclans.api.extension.ExtensionMeta;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Base class for all Bukkit/Paper-based ByteClans extensions.
 *
 * <p>Extend this class instead of implementing {@link ClanExtension} directly
 * when targeting Bukkit or Paper. It provides zero-boilerplate access to the
 * ByteClans API, the Bukkit plugin instance, logger, and data folder.
 *
 * <p>Example:
 * <pre>{@code
 * public final class WarExtension extends BukkitClanExtension {
 *
 *     @Override
 *     public void onEnable() {
 *         Bukkit.getPluginManager().registerEvents(new WarListener(), javaPlugin());
 *         this.logger().info("WarExtension enabled.");
 *     }
 *
 *     @Override
 *     public void onDisable() {
 *         this.logger().info("WarExtension disabled.");
 *     }
 * }
 * }</pre>
 *
 * <p>No constructor is needed — the context is injected automatically by the
 * extension loader before {@code onEnable()} is called.
 *
 * @see BukkitExtensionContext
 */
@ApiStatus.Experimental
public abstract class BukkitClanExtension implements ClanExtension {

    private BukkitExtensionContext bukkitExtensionContext;
    private ExtensionMeta extensionMeta;

    /**
     * Injects the extension context and metadata into this extension.
     *
     * @apiNote Internal use only. Called by the extension loader before {@code onEnable()}.
     * Do not call this method directly.
     */
    @ApiStatus.Internal
    public final void inject(
            final @NotNull BukkitExtensionContext bukkitExtensionContext,
            final @NotNull ExtensionMeta extensionMeta
    ) {
        this.bukkitExtensionContext = bukkitExtensionContext;
        this.extensionMeta = extensionMeta;
    }

    /**
     * Called when this extension is enabled.
     * Override this method to register listeners, commands, and any initialization logic.
     */
    @Override
    public void onEnable() {}

    /**
     * Called when this extension is disabled.
     * Override this method to clean up resources, cancel tasks, and save data.
     */
    @Override
    public void onDisable() {}

    /**
     * Returns the metadata declared in this extension's {@code extension.yml}.
     *
     * @return the {@link ExtensionMeta} for this extension.
     */
    @Override
    public ExtensionMeta extensionMeta() {
        return this.extensionMeta;
    }

    /**
     * Returns the ByteClans API entry point.
     *
     * @return the {@link ByteClans} API instance.
     */
    public ByteClans api() {
        return this.bukkitExtensionContext.api();
    }

    /**
     * Returns the raw {@link BukkitExtensionContext} for this extension.
     * Prefer using the helper methods on this class over accessing the context directly.
     *
     * @return the {@link BukkitExtensionContext} for this extension.
     */
    public BukkitExtensionContext clanExtensionContext() {
        return this.bukkitExtensionContext;
    }

    /**
     * Returns the {@link JavaPlugin} instance of ByteClans.
     * Use this to register Bukkit listeners, schedulers, and commands.
     *
     * @return the ByteClans {@link JavaPlugin} instance.
     */
    public JavaPlugin javaPlugin() {
        return this.bukkitExtensionContext.javaPlugin();
    }

    /**
     * Returns a {@link Logger} scoped to this extension.
     *
     * @return the logger for this extension.
     */
    public Logger logger() {
        return this.bukkitExtensionContext.logger();
    }

    /**
     * Returns the data folder assigned to this extension.
     * Located at {@code plugins/ByteClans/extensions/<ExtensionName>/}.
     *
     * @return the {@link Path} to this extension's data folder.
     */
    public Path dataFolder() {
        return this.bukkitExtensionContext.dataFolder();
    }

}