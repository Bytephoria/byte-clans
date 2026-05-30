package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.SetClanPvPModeAction;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class SetClanPvPModeActionLoader extends ActionLoader {

    @Contract(value = " -> new", pure = true)
    @Override
    public @NonNull @Unmodifiable List<String> getKeys() {
        return Collections.singletonList("set-clan-pvp-mode");
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    @Override
    public @NonNull Action load(
            final @NotNull String path,
            final @NotNull TypedMapAccessor accessor,
            final @NotNull File file
    ) {
        final String pvpMode = accessor.getString("pvp-mode", ClanPvPMode.NO_DAMAGE.name());
        return new SetClanPvPModeAction(pvpMode);
    }
}
