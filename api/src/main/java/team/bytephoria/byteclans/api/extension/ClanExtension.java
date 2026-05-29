package team.bytephoria.byteclans.api.extension;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a ByteClans extension.
 *
 * <p>Extensions are loaded from the {@code extensions/} folder inside the ByteClans
 * plugin directory. Each extension is a {@code .jar} file containing a class that
 * implements this interface (or extends {@code BukkitClanExtension}) and an
 * {@code extension.yml} descriptor.
 *
 * <p>Lifecycle order:
 * <ol>
 *   <li>{@link #onEnable()} — called after the extension is loaded and injected.</li>
 *   <li>{@link #onDisable()} — called when the plugin shuts down or the extension is unloaded.</li>
 * </ol>
 *
 * <p>Do not implement this interface directly if you are targeting Bukkit/Paper —
 * extend {@code BukkitClanExtension} instead, which provides access to
 * {@code JavaPlugin}, logger, and data folder out of the box.
 */
@ApiStatus.Experimental
public interface ClanExtension {

    /**
     * Called when this extension is enabled.
     * Use this method to register listeners, commands, and any other initialization logic.
     */
    void onEnable();

    /**
     * Called when this extension is disabled.
     * Use this method to clean up resources, cancel tasks, and save data.
     */
    void onDisable();

    /**
     * Returns the metadata declared in this extension's {@code extension.yml}.
     *
     * @return the {@link ExtensionMeta} for this extension.
     */
    ExtensionMeta extensionMeta();

}