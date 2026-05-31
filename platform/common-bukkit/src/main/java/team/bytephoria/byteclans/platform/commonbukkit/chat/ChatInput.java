package team.bytephoria.byteclans.platform.commonbukkit.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.ClanGlobalSettings;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ChatInput {

    private final Map<UUID, InputSession> sessions = new ConcurrentHashMap<>();

    private final JavaPlugin javaPlugin;
    private final Supplier<Listener> listenerSupplier;
    private final ClanGlobalSettings clanGlobalSettings;

    private Listener registeredListener;

    public ChatInput(
            final @NotNull JavaPlugin javaPlugin,
            final @NotNull Supplier<Listener> listenerSupplier,
            final @NotNull ClanGlobalSettings clanGlobalSettings
    ) {
        this.javaPlugin = javaPlugin;
        this.listenerSupplier = listenerSupplier;
        this.clanGlobalSettings = clanGlobalSettings;
    }

    public record InputSession(
            @NotNull Consumer<String> consumer,
            @NotNull Runnable onTimeout,
            int timeoutTaskId
    ) {}

    public void clear() {
        this.sessions.clear();
        this.unregisterListener();
    }

    public void register(
            final @NotNull Player player,
            final @NotNull Consumer<String> consumer,
            final @NotNull Runnable onTimeout
    ) {

        this.ensureListenerRegistered();

        final UUID uuid = player.getUniqueId();
        final Duration timeoutDuration = this.clanGlobalSettings.clanCreationTimeout();
        final long ticks = Math.max(1L, timeoutDuration.toMillis() / 50L);
        final int timeoutTaskId = this.javaPlugin.getServer()
                .getScheduler()
                .runTaskLater(this.javaPlugin, () -> {
                    this.sessions.remove(uuid);
                    onTimeout.run();
                    this.unregisterListenerIfUnused();
                }, ticks)
                .getTaskId();

        this.sessions.put(
                uuid,
                new InputSession(consumer, onTimeout, timeoutTaskId)
        );
    }

    public boolean handleMessage(
            final @NotNull Player player,
            final @Nullable String message
    ) {

        final InputSession session = this.sessions.remove(player.getUniqueId());
        if (session == null) {
            return false;
        }

        this.javaPlugin.getServer()
                .getScheduler()
                .cancelTask(session.timeoutTaskId());

        this.unregisterListenerIfUnused();

        if (message != null) {
            session.consumer().accept(message);
        }

        return true;
    }

    public void remove(final @NotNull UUID uuid) {
        final InputSession session = this.sessions.remove(uuid);
        if (session == null) {
            return;
        }

        this.javaPlugin.getServer()
                .getScheduler()
                .cancelTask(session.timeoutTaskId());

        this.unregisterListenerIfUnused();
    }

    public void remove(final @NotNull Player player) {
        this.remove(player.getUniqueId());
    }

    private void ensureListenerRegistered() {
        if (this.registeredListener != null) {
            return;
        }

        this.registeredListener = this.listenerSupplier.get();

        this.javaPlugin.getServer()
                .getPluginManager()
                .registerEvents(this.registeredListener, this.javaPlugin);
    }

    private void unregisterListenerIfUnused() {
        if (!this.sessions.isEmpty()) {
            return;
        }

        this.unregisterListener();
    }

    private void unregisterListener() {
        if (this.registeredListener == null) {
            return;
        }

        HandlerList.unregisterAll(this.registeredListener);
        this.registeredListener = null;
    }

}