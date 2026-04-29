package team.bytephoria.byteclans.infrastructure.configuration.configuration.invitations;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@ConfigSerializable
public final class Ttl {

    @Setting("amount")
    private int amount = 1;

    @Setting("unit")
    private TimeUnit unit = TimeUnit.MINUTES;

    public Duration toDuration() {
        return Duration.of(this.amount, this.unit.toChronoUnit());
    }

    public int amount() {
        return this.amount;
    }

    public TimeUnit unit() {
        return this.unit;
    }

}