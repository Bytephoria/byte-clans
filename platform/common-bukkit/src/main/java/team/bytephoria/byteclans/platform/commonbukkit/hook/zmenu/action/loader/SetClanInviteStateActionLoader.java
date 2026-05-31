package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.loader;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action.SetClanInviteStateAction;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class SetClanInviteStateActionLoader extends ActionLoader {

    @Contract(value = " -> new", pure = true)
    @Override
    public @NonNull @Unmodifiable List<String> getKeys() {
        return Collections.singletonList("set-clan-invite-state");
    }

    @Override
    public @NotNull Action load(
            final @NotNull String path,
            final @NotNull TypedMapAccessor accessor,
            final @NotNull File file
    ) {
        final String inviteState = accessor.getString("invite-state", ClanInviteState.INVITE_ONLY.name());
        return new SetClanInviteStateAction(inviteState);
    }
}
