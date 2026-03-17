package team.bytephoria.byteclans.infrastructure.configuration.roles;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public final class Roles {

    @Setting("roles")
    private Map<String, Role> roles = new HashMap<>();

    public Map<String, Role> roles() {
        return this.roles;
    }


}
