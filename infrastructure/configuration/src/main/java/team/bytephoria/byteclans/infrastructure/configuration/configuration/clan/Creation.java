package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.invitations.Ttl;

@ConfigSerializable
public final class Creation {

    @Setting("timeout")
    private Ttl timeout = new Ttl();

    public Ttl timeout() {
        return this.timeout;
    }
}
