package team.bytephoria.byteclans.platform.paper;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.commonbukkit.AudienceProvider;

public final class PaperAudienceProvider implements AudienceProvider {

    public static final PaperAudienceProvider INSTANCE = new PaperAudienceProvider();

    @Override
    public @NotNull Audience audience(final @NotNull Player player) {
        return player;
    }
}
