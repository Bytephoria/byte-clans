package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Name {

    @Setting("minimum-chars")
    private int minimumChars = 3;

    @Setting("maximum-chars")
    private int maximumChars = 16;

    public int minimumChars() {
        return this.minimumChars;
    }

    public int maximumChars() {
        return this.maximumChars;
    }

}