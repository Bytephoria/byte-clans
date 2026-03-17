package team.bytephoria.byteclans.api.access;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ByteClansProvider {

    private static ByteClans instance;

    private ByteClansProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static void setInstance(final @NotNull ByteClans instance) {
        final ByteClans notNullInstance = Objects.requireNonNull(instance, "ByteClansProvider instance must not be null");
        if (ByteClansProvider.instance != null) {
            throw new IllegalStateException("ByteClansProvider instance is already set");
        }

        ByteClansProvider.instance = notNullInstance;
    }

    public static @NotNull ByteClans getInstance() {
        return Objects.requireNonNull(instance, "ByteClans API is not available yet");
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static void resetInstance() {
        ByteClansProvider.instance = null;
    }

}
