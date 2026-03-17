package team.bytephoria.byteclans.spi.storage.transaction;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface TransactionManager {

    CompletableFuture<Void> execute(final @NotNull TransactionBlock block);

}