package team.bytephoria.byteclans.bukkitapi.access;

import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.access.ByteClansProvider;

public interface BukkitByteClans extends ByteClans {

    static @NotNull BukkitByteClans getAPI() {
        return (BukkitByteClans) ByteClansProvider.getInstance();
    }

    @NotNull CommandManager<Player> commandManager();
    @NotNull AnnotationParser<Player> annotationParser();

}
