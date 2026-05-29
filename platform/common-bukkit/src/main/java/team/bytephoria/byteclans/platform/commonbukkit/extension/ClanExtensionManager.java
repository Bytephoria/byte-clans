package team.bytephoria.byteclans.platform.commonbukkit.extension;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.extension.ClanExtension;
import team.bytephoria.byteclans.api.extension.ExtensionMeta;
import team.bytephoria.byteclans.bukkitapi.extension.BukkitClanExtension;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClanExtensionManager {

    private final Map<String, BukkitClanExtension> extensionMap = new HashMap<>();

    private final Logger logger;
    public ClanExtensionManager(final @NotNull Logger logger) {
        this.logger = logger;
    }

    public void loadAll(
            final @NotNull JavaPlugin javaPlugin,
            final @NotNull ClassLoader classLoader,
            final @NotNull ByteClans byteClans
    ) {
        final File extensionsDirectory = new File(javaPlugin.getDataFolder(), "extensions");
        if (extensionsDirectory.mkdirs()) {
            this.logger().info("Using default extensions folder.");
        }

        if (!extensionsDirectory.isDirectory()) {
            this.logger().warning("ExtensionFolder is not a directory:");
            return;
        }

        final File[] extensionsJar = extensionsDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (extensionsJar == null || extensionsJar.length == 0) {
            this.logger().warning("No extensions found in " + extensionsDirectory.getAbsolutePath());
            return;
        }

        this.logger().info("Found " + extensionsJar.length + " extensions.");
        for (final File extensionJar : extensionsJar) {
            this.load(extensionJar, extensionsDirectory, javaPlugin, classLoader, byteClans);
        }

    }

    public void load(
            final @NotNull File extensionJar,
            final @NotNull File extensionsDirectory,
            final @NotNull JavaPlugin javaPlugin,
            final @NotNull ClassLoader classLoader,
            final @NotNull ByteClans byteClans
    ) {

        this.logger().info("Loading extension " + extensionJar.getName());

        try {
            final ExtensionMeta metadata = ExtensionLoader.loadMetadata(extensionJar);
            final BukkitClanExtension extension = ExtensionLoader.loadExtension(
                    javaPlugin,
                    classLoader,
                    byteClans,
                    extensionJar,
                    extensionsDirectory,
                    metadata
            );

            this.extensionMap.put(metadata.name(), extension);
            this.logger().info("Loaded extension " + metadata.name());
        } catch (RuntimeException | MalformedURLException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException | ClassNotFoundException e
        ) {
            this.logger().log(Level.SEVERE, "Failed to load extension " + extensionJar.getName(), e);
        }
    }

    public void enableAll() {
        for (final ClanExtension paperExtension : this.extensionMap.values()) {
            paperExtension.onEnable();
        }
    }

    public void unloadAll() {
        for (final ClanExtension extension : this.extensionMap.values()) {
            extension.onDisable();
        }

        this.extensionMap.clear();
    }

    @Contract(pure = true)
    public @NonNull Collection<BukkitClanExtension> registered() {
        return this.extensionMap.values();
    }

    public @NotNull Logger logger() {
        return this.logger;
    }

}
