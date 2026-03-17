package team.bytephoria.byteclans.infrastructure.configuration.roles;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public final class Role {

    @Setting("display-name")
    private String displayName = "<gray>Member";

    @Setting("priority")
    private int priority = 1;

    @Setting("default")
    private boolean isDefault = false;

    @Setting("actions")
    private List<String> actions = new ArrayList<>();

    public String displayName() {
        return this.displayName;
    }

    public int priority() {
        return this.priority;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public List<String> actions() {
        return this.actions;
    }

}