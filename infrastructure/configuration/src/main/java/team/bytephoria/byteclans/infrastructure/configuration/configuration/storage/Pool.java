package team.bytephoria.byteclans.infrastructure.configuration.configuration.storage;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Pool {

    @Setting("connection-timeout")
    private long connectionTimeout = 5000;

    @Setting("max-lifetime")
    private long maxLifetime = 1800000;

    @Setting("max-pool-size")
    private int maxPoolSize = 10;

    @Setting("min-idle")
    private int minIdle = 1;

    public long connectionTimeout() {
        return this.connectionTimeout;
    }

    public long maxLifetime() {
        return this.maxLifetime;
    }

    public int maxPoolSize() {
        return this.maxPoolSize;
    }

    public int minIdle() {
        return this.minIdle;
    }

}