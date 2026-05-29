package team.bytephoria.byteclans.api.extension;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the metadata declared in an extension's {@code extension.yml} descriptor.
 *
 * <p>Example {@code extension.yml}:
 * <pre>{@code
 * name: GuerraExtension
 * version: 1.0.0
 * author: yourname
 * description: Adds clan wars to ByteClans.
 * main: com.example.GuerraExtension
 * }</pre>
 */
@ApiStatus.Experimental
public interface ExtensionMeta {

    /**
     * The unique name of this extension, as declared in {@code extension.yml}.
     * Used as the key in the extension registry.
     */
    @NotNull String name();

    /**
     * The version string of this extension (e.g. {@code "1.0.0"}).
     */
    @NotNull String version();

    /**
     * A short human-readable description of what this extension does.
     */
    @NotNull String description();

    /**
     * The author of this extension.
     */
    @NotNull String author();

    /**
     * The fully qualified class name of the extension's main class.
     * This class must extend {@code BukkitClanExtension} (or implement {@link ClanExtension}).
     *
     * <p>Example: {@code com.example.WarExtension}
     */
    @NotNull String main();

}