package team.bytephoria.byteclans.platform.commonbukkit.extension;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.extension.ExtensionMeta;
import team.bytephoria.byteclans.bukkitapi.extension.BukkitClanExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class ExtensionLoader {

    static @NotNull BukkitClanExtension loadExtension(
            final @NotNull JavaPlugin javaPlugin,
            final @NotNull ClassLoader classLoader,
            final @NotNull ByteClans byteClans,
            final @NotNull File file,
            final @NotNull File extensionsDirectory,
            final @NotNull ExtensionMeta extensionMeta
    ) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String extensionName = extensionMeta.name();
        final String extensionMain = extensionMeta.main();

        final URLClassLoader urlClassLoader = new URLClassLoader(
                new URL[]{file.toURI().toURL()},
                classLoader
        );

        final Class<?> mainClass = Class.forName(extensionMain, true, urlClassLoader);
        if (!BukkitClanExtension.class.isAssignableFrom(mainClass)) {
            throw new IllegalStateException("Main class is not a BukkitClanExtension: " + extensionMain);
        }

        final BukkitClanExtensionContext bukkitClanExtensionContext = new BukkitClanExtensionContext(
                extensionMeta,
                javaPlugin,
                byteClans,
                javaPlugin.getLogger(),
                new File(extensionsDirectory, extensionName).toPath()
        );

        try {
            final BukkitClanExtension bukkitClanExtension = (BukkitClanExtension) mainClass
                    .getDeclaredConstructor()
                    .newInstance();

            bukkitClanExtension.inject(bukkitClanExtensionContext, extensionMeta);
            return bukkitClanExtension;
        } catch (NoSuchMethodException ignored) {
            throw new NoSuchMethodException("No such method: " + extensionMain);
        }

    }

    static @NotNull ExtensionMeta loadMetadata(final @NotNull File file) {
        try (final JarFile jarFile = new JarFile(file)) {
            final JarEntry jarEntry = jarFile.getJarEntry("extension.yml");
            if (jarEntry == null) {
                throw new RuntimeException("No extension.yml found in extension jar: " + file.getName());
            }

            try (final InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                final Yaml yaml = new Yaml();
                final Map<String, Object> objectMap = yaml.load(inputStream);

                return new DefaultExtensionMeta(
                        (String) objectMap.get("name"),
                        (String) objectMap.get("version"),
                        (String) objectMap.getOrDefault("description", ""),
                        (String) objectMap.getOrDefault("author", ""),
                        (String) objectMap.get("main")
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
