package team.bytephoria.byteclans.platform.commonbukkit.hook.zmenu.action;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.api.manager.ClanSettingsManager;

public final class SetClanInviteStateAction extends Action {

    private final String inviteState;
    public SetClanInviteStateAction(final @NotNull String inviteState) {
        this.inviteState = inviteState;
    }

    @Override
    protected void execute(
            final @NotNull Player player,
            final @NotNull Button button,
            final @NotNull InventoryEngine inventoryEngine,
            final @NotNull Placeholders placeholders
    ) {
        final ByteClans byteClans = ByteClans.getAPI();
        final ClanSettingsManager clanSettingsManager = byteClans.settingsManager();

        byteClans.getMember(player.getUniqueId())
                .ifPresent(clanMember -> {
                    final Clan clan = clanMember.clan();
                    if (clan == null) {
                        return;
                    }


                    ClanInviteState clanInviteState;
                    try {
                        clanInviteState = ClanInviteState.valueOf(this.inviteState.toUpperCase());
                    } catch (final IllegalArgumentException e) {
                        clanInviteState = ClanInviteState.INVITE_ONLY;
                    }

                    clanSettingsManager.changeInviteStatus(clanMember, clanInviteState);
                });

    }
}
