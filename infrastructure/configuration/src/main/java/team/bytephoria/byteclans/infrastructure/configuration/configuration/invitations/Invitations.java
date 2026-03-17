package team.bytephoria.byteclans.infrastructure.configuration.configuration.invitations;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Invitations {

    @Setting("ttl")
    private Ttl ttl = new Ttl();

    public Ttl ttl() {
        return this.ttl;
    }
}