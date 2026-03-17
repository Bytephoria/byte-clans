package team.bytephoria.byteclans.api.util.response.context;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

record FailureResponseContext<T, R>(R result) implements ResponseContext<T, R> {

    FailureResponseContext {
        Objects.requireNonNull(result, "result cannot be null.");
    }

    @Contract(pure = true)
    @Override
    public @Nullable T value() {
        return null;
    }

    @Override
    public boolean success() {
        return false;
    }
}
