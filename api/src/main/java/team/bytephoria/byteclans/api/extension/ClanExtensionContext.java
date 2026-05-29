package team.bytephoria.byteclans.api.extension;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Provides contextual information and services to a {@link ClanExtension}.
 *
 * <p>An instance of this context is injected into the extension before {@code onEnable()}
 * is called. Extensions should access it via the helper methods provided by their base class
 * (e.g. {@code BukkitClanExtension}) rather than storing the context manually.
 *
 * <p>The context is the single entry point to everything ByteClans exposes to extensions:
 * metadata, the Clan API, logger, data folder, and configuration.
 */
@ApiStatus.Experimental
public interface ClanExtensionContext {

    /**
     * Returns the metadata declared in this extension's {@code extension.yml}.
     *
     * @return the {@link ExtensionMeta} for this extension.
     */
    @NotNull ExtensionMeta extensionMeta();

    /**
     * Returns a {@link Logger} scoped to this extension.
     * Log messages will be prefixed with the extension's name.
     *
     * @return the logger for this extension.
     */
    @NotNull Logger logger();

    /**
     * Returns the data folder assigned to this extension.
     *
     * <p>This folder is located at {@code plugins/ByteClans/extensions/<ExtensionName>/}
     * and is created automatically if it does not exist. Extensions should store
     * their config files, databases, and any persistent data here.
     *
     * @return the {@link Path} to this extension's data folder.
     */
    @NotNull Path dataFolder();

}