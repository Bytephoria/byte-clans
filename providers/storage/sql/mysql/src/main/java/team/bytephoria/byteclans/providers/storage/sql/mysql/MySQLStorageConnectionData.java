package team.bytephoria.byteclans.providers.storage.sql.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.SQLStorageConnectionData;

public final class MySQLStorageConnectionData extends SQLStorageConnectionData {

    MySQLStorageConnectionData() {}

    @Contract(value = " -> new", pure = true)
    public static @NotNull MySQLStorageConnectionData builder() {
        return new MySQLStorageConnectionData();
    }

    @Override
    public @NotNull HikariDataSource build() {
        final HikariConfig hikari = new HikariConfig();

        hikari.setJdbcUrl(
                "jdbc:mysql://" + this.jdbcCredentials.hostname() +
                        ":" + this.jdbcCredentials.port() +
                        "/" + this.jdbcCredentials.database() +
                        "?useSSL=" + this.jdbcCredentials.useSSL()
        );

        hikari.setUsername(this.jdbcCredentials.username());
        hikari.setPassword(this.jdbcCredentials.password());

        hikari.setConnectionTimeout(this.jdbcPoolConfig.connectionTimeoutMs());
        hikari.setMaxLifetime(this.jdbcPoolConfig.maxLifeTimeMs());
        hikari.setMaximumPoolSize(this.jdbcPoolConfig.maxPoolSize());
        hikari.setMinimumIdle(this.jdbcPoolConfig.minIdle());
        hikari.setPoolName("ByteClans-MySQLPool");

        return new HikariDataSource(hikari);
    }
}
