package team.bytephoria.byteclans.platform.commonbukkit;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.commonbukkit.chat.ChatInput;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public interface CommonBukkitFacade {

    @NotNull ChatInput chatInput();

    @NotNull Executor mainThreadExecutor();

    @NotNull AudienceProvider audienceProvider();

    @NotNull CompletableFuture<Void> runAsync(
            final @NotNull Runnable callable
    );

    @NotNull <T> CompletableFuture<T> supplyAsync(
            final @NotNull Supplier<T> callable
    );

}
