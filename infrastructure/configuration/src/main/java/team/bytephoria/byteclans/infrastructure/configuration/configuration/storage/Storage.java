package team.bytephoria.byteclans.infrastructure.configuration.configuration.storage;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Storage {

    @Setting("type")
    private String type = "H2";

    @Setting("pool")
    private Pool pool = new Pool();

    @Setting("credentials")
    private Credentials credentials = new Credentials();

    @Setting("h2")
    private H2 h2 = new H2();

    public String type() {
        return this.type;
    }

    public Pool pool() {
        return this.pool;
    }

    public Credentials credentials() {
        return this.credentials;
    }

    public H2 h2() {
        return this.h2;
    }

}