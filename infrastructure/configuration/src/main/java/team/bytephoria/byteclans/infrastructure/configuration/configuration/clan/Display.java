package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.invitations.Ttl;

@ConfigSerializable
public final class Display {

    @Setting("cooldown")
    private Ttl cooldown = new Ttl();

    public Ttl cooldown() {
        return this.cooldown;
    }
}
