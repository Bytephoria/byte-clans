package team.bytephoria.byteclans.providers.storage.sql.h2;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.SQLStorageConnectionData;

import java.io.File;

public final class H2StorageConnectionData extends SQLStorageConnectionData {

    private File file;

    H2StorageConnectionData() {}

    @Contract(" -> new")
    public static @NotNull H2StorageConnectionData builder() {
        return new H2StorageConnectionData();
    }

    public H2StorageConnectionData file(final @NotNull File file) {
        this.file = file;
        return this;
    }

    public @NotNull HikariDataSource build() {
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");

        final String basePath = this.file.getAbsolutePath().replace("\\", "/");
        final String jdbcUrl =
                "jdbc:h2:file:" + basePath +
                        ";MODE=MySQL" +
                        ";DATABASE_TO_UPPER=FALSE" +
                        ";DB_CLOSE_DELAY=-1" +
                        ";DB_CLOSE_ON_EXIT=FALSE";

        hikariConfig.addDataSourceProperty("URL", jdbcUrl);

        hikariConfig.setUsername(this.jdbcCredentials.username());
        hikariConfig.setPassword(this.jdbcCredentials.password());

        hikariConfig.setConnectionTimeout(this.jdbcPoolConfig.connectionTimeoutMs());
        hikariConfig.setMaxLifetime(this.jdbcPoolConfig.maxLifeTimeMs());
        hikariConfig.setMaximumPoolSize(this.jdbcPoolConfig.maxPoolSize());
        hikariConfig.setMinimumIdle(this.jdbcPoolConfig.minIdle());
        hikariConfig.setPoolName("ByteClans-H2Pool");

        return new HikariDataSource(hikariConfig);
    }
}
