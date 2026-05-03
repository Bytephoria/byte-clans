package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan.points;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Points {

    @Setting("limits")
    private Limits limits = new Limits();

    @Setting("actions")
    private Actions actions = new Actions();

    public Limits limits() {
        return this.limits;
    }

    public Actions actions() {
        return this.actions;
    }
}
