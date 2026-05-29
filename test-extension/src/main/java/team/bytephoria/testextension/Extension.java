package team.bytephoria.testextension;

import team.bytephoria.byteclans.bukkitapi.extension.BukkitClanExtension;

import java.io.File;

public final class Extension extends BukkitClanExtension {

    @Override
    public void onEnable() {
        this.logger().info("[PaperExtension] Enabled");

        final File dataFolder = this.dataFolder().toFile();
        if (dataFolder.mkdirs()) {
            this.javaPlugin().getSLF4JLogger().info("Folder created.");
        }

    }

    @Override
    public void onDisable() {
        this.logger().info("[PaperExtension] Disabled");
    }
}
