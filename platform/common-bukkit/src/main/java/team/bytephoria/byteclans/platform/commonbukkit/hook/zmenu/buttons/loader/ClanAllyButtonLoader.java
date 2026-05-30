package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.buttons.ClanAllyButton;

public final class ClanAllyButtonLoader extends ButtonLoader {

    public ClanAllyButtonLoader(final @NotNull Plugin plugin) {
        super(plugin, "BYTECLANS_CLAN_ALLY_ITEM");
    }

    @Override
    public @NotNull Button load(
            final @NotNull YamlConfiguration configuration,
            final @NotNull String path,
            final @NotNull DefaultButtonValue defaultButtonValue
    ) {
        return new ClanAllyButton();
    }
}
