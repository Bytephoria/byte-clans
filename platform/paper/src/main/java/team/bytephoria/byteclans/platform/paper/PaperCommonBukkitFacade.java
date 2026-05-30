package team.bytephoria.byteclans.platform.paper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.bytephoria.byteclans.platform.commonbukkit.AudienceProvider;
import team.bytephoria.byteclans.platform.commonbukkit.CommonBukkitFacade;
import team.bytephoria.byteclans.platform.commonbukkit.chat.ChatInput;
import team.bytephoria.byteclans.platform.commonbukkit.concurrent.AsyncExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class PaperCommonBukkitFacade implements CommonBukkitFacade {

    private final PaperPlugin paperPlugin;
    public PaperCommonBukkitFacade(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @Override
    public @NotNull ChatInput chatInput() {
        return this.paperPlugin.chatInput();
    }

    @Override
    public @NotNull Executor mainThreadExecutor() {
        return this.paperPlugin.getServer().getScheduler().getMainThreadExecutor(this.paperPlugin);
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable AudienceProvider audienceProvider() {
        return this.paperPlugin.nativeAudienceProvider();
    }

    @Override
    public @NotNull CompletableFuture<Void> runAsync(final @NotNull Runnable callable) {
        return AsyncExecutor.runAsync(callable);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> supplyAsync(final @NotNull Supplier<T> callable) {
        return AsyncExecutor.supplyAsync(callable);
    }
}
