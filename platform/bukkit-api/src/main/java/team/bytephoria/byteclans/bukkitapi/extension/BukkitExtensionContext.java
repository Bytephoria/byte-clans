package team.bytephoria.byteclans.bukkitapi.extension;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.extension.ClanExtensionContext;

/**
 * A Bukkit-specific extension of {@link ClanExtensionContext} that provides
 * access to Bukkit/Paper internals alongside the ByteClans API.
 *
 * <p>This context is injected into {@link BukkitClanExtension} instances before
 * {@code onEnable()} is called. Prefer using the helper methods on
 * {@link BukkitClanExtension} directly rather than accessing the context manually.
 *
 * @see BukkitClanExtension
 */
@ApiStatus.Experimental
public interface BukkitExtensionContext extends ClanExtensionContext {

    /**
     * Returns the {@link JavaPlugin} instance of ByteClans.
     *
     * <p>Use this to register Bukkit listeners, schedulers, and commands:
     * <pre>{@code
     * Bukkit.getPluginManager().registerEvents(new MyListener(), javaPlugin());
     * Bukkit.getScheduler().runTaskTimer(javaPlugin(), this::tick, 0L, 20L);
     * }</pre>
     *
     * @return the ByteClans {@link JavaPlugin} instance.
     */
    @NotNull JavaPlugin javaPlugin();

    /**
     * Returns the ByteClans API entry point.
     *
     * <p>Use this to interact with clans, players, and other ByteClans services:
     * <pre>{@code
     * api().getClan().ifPresent(clan -> ...);
     * }</pre>
     *
     * @return the {@link ByteClans} API instance.
     */
    @NotNull ByteClans api();

}