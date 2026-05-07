package team.bytephoria.byteclans.providers.storage.sql.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.SQLStorageConnectionData;

public final class MariaDBStorageConnectionData extends SQLStorageConnectionData {

    MariaDBStorageConnectionData() {}

    @Contract(value = " -> new", pure = true)
    public static @NotNull MariaDBStorageConnectionData builder() {
        return new MariaDBStorageConnectionData();
    }


    @Override
    public @NotNull HikariDataSource build() {
        final HikariConfig hikari = new HikariConfig();

        hikari.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikari.addDataSourceProperty("url",
                "jdbc:mariadb://" + this.jdbcCredentials.hostname() + ":" +
                        this.jdbcCredentials.port() + "/" + this.jdbcCredentials.database() +
                        "?useSSL=" + this.jdbcCredentials.useSSL());

        hikari.addDataSourceProperty("user", this.jdbcCredentials.username());
        hikari.addDataSourceProperty("password", this.jdbcCredentials.password());

        hikari.setConnectionTimeout(this.jdbcPoolConfig.connectionTimeoutMs());
        hikari.setMaxLifetime(this.jdbcPoolConfig.maxLifeTimeMs());
        hikari.setMaximumPoolSize(this.jdbcPoolConfig.maxPoolSize());
        hikari.setMinimumIdle(this.jdbcPoolConfig.minIdle());
        hikari.setPoolName("ByteBalloons-MariaDBPool");

        return new HikariDataSource(hikari);
    }
}
