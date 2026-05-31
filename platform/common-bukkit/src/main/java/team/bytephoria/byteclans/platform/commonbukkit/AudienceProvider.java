package team.bytephoria.byteclans.platform.commonbukkit;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface AudienceProvider {

    @NotNull Audience audience(final @NotNull Player player);

}
