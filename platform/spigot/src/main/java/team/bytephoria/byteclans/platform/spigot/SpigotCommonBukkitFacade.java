package team.bytephoria.byteclans.platform.spigot;

import org.bukkit.Bukkit;
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

public final class SpigotCommonBukkitFacade implements CommonBukkitFacade {

    private final SpigotPlugin spigotPlugin;
    public SpigotCommonBukkitFacade(final @NotNull SpigotPlugin spigotPlugin) {
        this.spigotPlugin = spigotPlugin;
    }

    @Override
    public @NotNull ChatInput chatInput() {
        return this.spigotPlugin.chatInput();
    }

    @Override
    public @NotNull Executor mainThreadExecutor() {
        return runnable -> {
            if (Bukkit.isPrimaryThread()) {
                runnable.run();
                return;
            }

            Bukkit.getScheduler().runTask(this.spigotPlugin, runnable);
        };
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable AudienceProvider audienceProvider() {
        return this.spigotPlugin.spigotAudienceProvider();
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
