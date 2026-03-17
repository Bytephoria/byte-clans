package team.bytephoria.byteclans.providers.storage.sql;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionBlock;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public final class SQLTransactionManager implements TransactionManager {

    private final AbstractSQLStorageConnection storageConnection;
    private final ExecutorService executorService;

    public SQLTransactionManager(
            final @NotNull AbstractSQLStorageConnection storageConnection,
            final @NotNull ExecutorService executorService
    ) {
        this.storageConnection = storageConnection;
        this.executorService = executorService;
    }

    @Override
    public @NotNull CompletableFuture<Void> execute(final @NotNull TransactionBlock block) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.storageConnection.withTransaction(connection -> {
                    block.run();
                    return null;
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, this.executorService);
    }
}