package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan.points;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class Limits {

    private int minimum = 0;
    private int maximum = -1;

    public int minimum() {
        return this.minimum;
    }

    public int maximum() {
        return this.maximum;
    }
}
