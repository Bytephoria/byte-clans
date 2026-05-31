package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.bytephoria.byteclans.platform.commonbukkit.CommonBukkitFacade;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.ClanCreateAction;
import team.bytephoria.byteclans.platform.commonbukkit.messages.Messenger;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class ClanCreateActionLoader extends ActionLoader {

    private final CommonBukkitFacade commonBukkitFacade;
    private final Messenger messenger;

    public ClanCreateActionLoader(
            final @NotNull CommonBukkitFacade commonBukkitFacade,
            final @NotNull Messenger messenger
    ) {
        this.commonBukkitFacade = commonBukkitFacade;
        this.messenger = messenger;
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> getKeys() {
        return Collections.singletonList("clan-create");
    }

    @Override
    public @NotNull Action load(
            final @NotNull String path,
            final @NotNull TypedMapAccessor accessor,
            final @NotNull File file
    ) {
        return new ClanCreateAction(this.commonBukkitFacade, this.messenger);
    }
}
