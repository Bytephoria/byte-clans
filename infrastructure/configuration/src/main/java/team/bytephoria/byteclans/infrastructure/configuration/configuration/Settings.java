package team.bytephoria.byteclans.infrastructure.configuration.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Settings {

    @Setting("serializer")
    private String serializer = "MINI_MESSAGE";

    public String serializer() {
        return this.serializer;
    }
}