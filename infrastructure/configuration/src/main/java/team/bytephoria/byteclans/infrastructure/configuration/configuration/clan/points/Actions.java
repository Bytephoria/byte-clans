package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan.points;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Actions {

    @Setting("kills")
    private int kills = 1;

    @Setting("deaths")
    private int deaths = -2;

    public int kills() {
        return this.kills;
    }

    public int deaths() {
        return this.deaths;
    }
}
