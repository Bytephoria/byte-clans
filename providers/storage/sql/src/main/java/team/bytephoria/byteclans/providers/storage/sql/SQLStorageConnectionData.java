package team.bytephoria.byteclans.providers.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.config.JdbcCredentials;
import team.bytephoria.byteclans.providers.storage.sql.config.JdbcPoolConfig;

public abstract class SQLStorageConnectionData {

    protected JdbcCredentials jdbcCredentials;
    protected JdbcPoolConfig jdbcPoolConfig;

    public SQLStorageConnectionData jdbcCredentials(final @NotNull JdbcCredentials jdbcCredentials) {
        this.jdbcCredentials = jdbcCredentials;
        return this;
    }

    public SQLStorageConnectionData jdbcPoolConfig(final @NotNull JdbcPoolConfig jdbcPoolConfig) {
        this.jdbcPoolConfig = jdbcPoolConfig;
        return this;
    }

    public abstract HikariDataSource build();



}
