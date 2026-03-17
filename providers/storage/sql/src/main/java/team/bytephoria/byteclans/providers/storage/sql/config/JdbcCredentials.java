package team.bytephoria.byteclans.providers.storage.sql.config;

import org.jetbrains.annotations.NotNull;

public final class JdbcCredentials {

    private final String hostname;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean useSSL;

    public JdbcCredentials(
            final @NotNull String hostname,
            final int port,
            final @NotNull String database,
            final @NotNull String username,
            final @NotNull String password,
            final boolean useSSL
    ) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.useSSL = useSSL;
    }

    public String hostname() {
        return this.hostname;
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

    public boolean useSSL() {
        return this.useSSL;
    }


}
