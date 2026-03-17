package team.bytephoria.byteclans.providers.storage.sql;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.spi.storage.ClanStorage;

import java.util.logging.Logger;

public abstract class AbstractSQLClanStorage implements ClanStorage {

    private final AbstractSQLStorageConnection storageConnection;
    private final Logger logger;

    public AbstractSQLClanStorage(
            final @NotNull AbstractSQLStorageConnection storageConnection,
            final @NotNull Logger logger
    ) {
        this.storageConnection = storageConnection;
        this.logger = logger;
    }

    public AbstractSQLStorageConnection storageConnection() {
        return this.storageConnection;
    }

    public Logger logger() {
        return this.logger;
    }
}
