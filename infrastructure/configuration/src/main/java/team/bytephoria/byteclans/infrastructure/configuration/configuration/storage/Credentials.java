package team.bytephoria.byteclans.infrastructure.configuration.configuration.storage;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class Credentials {

    @Setting("host")
    private String host = "localhost";

    @Setting("port")
    private int port = 3306;

    @Setting("database")
    private String database = "byteclans";

    @Setting("username")
    private String username = "root";

    @Setting("password")
    private String password = "";

    @Setting("use-ssl")
    private boolean useSsl = false;

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public String database() {
        return this.database;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public boolean useSsl() {
        return this.useSsl;
    }
}
