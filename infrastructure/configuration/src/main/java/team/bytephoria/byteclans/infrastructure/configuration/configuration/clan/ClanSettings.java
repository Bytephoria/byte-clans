package team.bytephoria.byteclans.infrastructure.configuration.configuration.clan;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class ClanSettings {

    @Setting("name")
    private Name name = new Name();

    @Setting("defaults")
    private Defaults defaults = new Defaults();

    @Setting("display")
    private Display display = new Display();

    @Setting("chat")
    private Chat chat = new Chat();

    public Name name() {
        return this.name;
    }

    public Defaults defaults() {
        return this.defaults;
    }

    public Display display() {
        return this.display;
    }

    public Chat chat() {
        return this.chat;
    }
}
