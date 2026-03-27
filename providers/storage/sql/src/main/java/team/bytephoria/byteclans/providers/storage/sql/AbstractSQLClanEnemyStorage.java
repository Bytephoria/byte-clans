package team.bytephoria.byteclans.providers.storage.sql;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.spi.storage.ClanEnemyStorage;

import java.util.logging.Logger;

public abstract class AbstractSQLClanEnemyStorage implements ClanEnemyStorage {

    private final AbstractSQLStorageConnection storageConnection;
    private final Logger logger;

    protected AbstractSQLClanEnemyStorage(
            final @NotNull AbstractSQLStorageConnection storageConnection,
            final @NotNull Logger logger
    ) {
        this.storageConnection = storageConnection;
        this.logger = logger;
    }

    protected AbstractSQLStorageConnection storageConnection() {
        return this.storageConnection;
    }

    protected Logger logger() {
        return this.logger;
    }
}