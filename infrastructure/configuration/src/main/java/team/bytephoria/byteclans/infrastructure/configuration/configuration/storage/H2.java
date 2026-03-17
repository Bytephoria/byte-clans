package team.bytephoria.byteclans.infrastructure.configuration.configuration.storage;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class H2 {

    @Setting("file")
    private String file = "data/byteclans";

    public String file() {
        return this.file;
    }
}