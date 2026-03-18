package team.bytephoria.byteclans.platform.spigot.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class AsyncExecutor {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("ByteClans-worker-", 0).factory()
    );

    public static @NotNull CompletableFuture<Void> runAsync(final @NotNull Runnable runnable) {
        return CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE);
    }

    public static @NotNull <T> CompletableFuture<T> supplyAsync(final @NotNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, EXECUTOR_SERVICE);
    }

    public static ExecutorService getExecutor() {
        return EXECUTOR_SERVICE;
    }

    public static void shutdown() {
        EXECUTOR_SERVICE.shutdown();
        try {
            final boolean terminated = EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS);
            if (!terminated) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            EXECUTOR_SERVICE.shutdownNow();
        }
    }

}
