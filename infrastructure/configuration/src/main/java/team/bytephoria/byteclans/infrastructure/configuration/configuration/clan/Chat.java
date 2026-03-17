package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Chat {

    @Setting("clan-format")
    private String clanFormat = "";

    public String clanFormat() {
        return this.clanFormat;
    }
}
