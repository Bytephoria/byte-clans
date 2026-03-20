package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;

@ConfigSerializable
public final class Defaults {

    @Setting("max-members")
    private int maxMembers = 20;

    @Setting("pvp-mode")
    private ClanPvPMode pvpMode = ClanPvPMode.NO_DAMAGE;

    @Setting("invite-state")
    private ClanInviteState inviteState = ClanInviteState.INVITE_ONLY;

    @Setting("display")
    private String display = "{clan}";

    public int maxMembers() {
        return this.maxMembers;
    }

    public ClanPvPMode pvpMode() {
        return this.pvpMode;
    }

    public ClanInviteState inviteState() {
        return this.inviteState;
    }

    public String display() {
        return this.display;
    }
}