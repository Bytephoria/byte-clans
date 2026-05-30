package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.SetClanDisplayAction;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class SetClanDisplayActionLoader extends ActionLoader {

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> getKeys() {
        return Collections.singletonList("set-clan-display");
    }

    @Override
    public @NotNull Action load(
            final @NotNull String path,
            final @NotNull TypedMapAccessor accessor,
            final @NotNull File file
    ) {
        final String display = accessor.getString("display");
        return new SetClanDisplayAction(display);
    }

}
