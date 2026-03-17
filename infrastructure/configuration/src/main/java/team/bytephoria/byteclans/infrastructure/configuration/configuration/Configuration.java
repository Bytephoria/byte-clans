package team.bytephoria.byteclans.infrastructure.configuration.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.clan.ClanSettings;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.invitations.Invitations;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.storage.Storage;

@ConfigSerializable
public final class Configuration {

    @Setting("storage")
    private Storage storage = new Storage();

    @Setting("settings")
    private Settings settings = new Settings();

    @Setting("clan")
    private ClanSettings clan = new ClanSettings();

    @Setting("invitations")
    private Invitations invitations = new Invitations();

    public Storage storage() {
        return storage;
    }

    public Settings settings() {
        return this.settings;
    }

    public ClanSettings clan() {
        return this.clan;
    }

    public Invitations invitations() {
        return this.invitations;
    }

}
