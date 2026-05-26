package team.bytephoria.byteclans.platform.commonbukkit.extension;

import org.bukkit.plugin.java.JavaPlugin;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.extension.ExtensionMeta;
import team.bytephoria.byteclans.bukkitapi.extension.BukkitExtensionContext;

import java.nio.file.Path;
import java.util.logging.Logger;

public record BukkitClanExtensionContext(
        ExtensionMeta extensionMeta,
        JavaPlugin javaPlugin,
        ByteClans api,
        Logger logger,
        Path dataFolder
) implements BukkitExtensionContext {

}
