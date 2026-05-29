package team.bytephoria.byteclans.api.access;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Static access point for the {@link ByteClans} API instance.
 * <p>
 * This provider follows a singleton-like pattern where the API instance
 * is assigned once during initialization and can later be globally accessed.
 */
public final class ByteClansProvider {

    /**
     * Stored API instance.
     */
    private static ByteClans instance;

    /**
     * Prevents instantiation of this utility class.
     */
    private ByteClansProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Sets the API instance.
     * <p>
     * This method is intended for internal use only and can only be called once.
     *
     * @param instance the API instance to register
     * @throws NullPointerException if the instance is null
     * @throws IllegalStateException if the instance was already set
     */
    @ApiStatus.Internal
    public static void setInstance(final @NotNull ByteClans instance) {
        final ByteClans notNullInstance = Objects.requireNonNull(
                instance,
                "ByteClansProvider instance must not be null"
        );

        if (ByteClansProvider.instance != null) {
            throw new IllegalStateException("ByteClansProvider instance is already set");
        }

        ByteClansProvider.instance = notNullInstance;
    }

    /**
     * Returns the current API instance.
     *
     * @return the registered {@link ByteClans} instance
     * @throws NullPointerException if the API is not yet available
     */
    public static @NotNull ByteClans getInstance() {
        return Objects.requireNonNull(
                instance,
                "ByteClans API is not available yet"
        );
    }

    /**
     * Checks whether the API instance has already been initialized.
     *
     * @return true if the instance exists, otherwise false
     */
    public static boolean hasInstance() {
        return instance != null;
    }

    /**
     * Resets the stored API instance.
     * <p>
     * Intended for internal/testing purposes only.
     */
    @ApiStatus.Internal
    public static void resetInstance() {
        ByteClansProvider.instance = null;
    }

}